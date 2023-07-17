package ba.fet.rwa.models;

import ba.fet.rwa.projections.PlayerQuestionProjection;
import ba.fet.rwa.projections.QuestionProjection;
import ba.fet.rwa.projections.QuizProjection;
import ba.fet.rwa.services.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

public class QuizSession {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private Quiz quiz;
    private long expirationTime;
    private Set<Player> players;
    private int currentQuestionIndex = -1;
    private long currentQuestionStartTime;
    private QuizSessionState state;
    private Session ownerSession;

    public QuizSession(Long quizId, long expirationTime) {
        state = QuizSessionState.WAITING_FOR_PLAYERS;
        QuizService quizService = new QuizService();
        this.quiz = quizService.getQuizById(quizId);
        this.expirationTime = expirationTime;
        this.players = new HashSet<>();
    }

    public void setOwnerSession(Session ownerSession) {
        if (this.ownerSession != null) {
            throw new RuntimeException("Owner session already set");
        } else {
            this.ownerSession = ownerSession;
        }
    }

    public void startQuiz() {
        currentQuestionIndex = 0;
        messageOwner(MessageType.QUIZ_STARTED);
        startCurrentQuestion();
    }

    private void incrementQuestionIndex() {
        currentQuestionIndex++;
    }

    public void startCurrentQuestion() {
        if (currentQuestionIndex == -1) {
            throw new RuntimeException("Quiz not started");
        }

        if (state.equals(QuizSessionState.FINISHED)) {
            throw new RuntimeException("Quiz already finished");
        }

        currentQuestionStartTime = System.currentTimeMillis();
        state = QuizSessionState.QUESTION_ACTIVE;
        broadcastCurrentQuestion();
        // after time is up, close question, calculate scores and broadcast them to players
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                closeCurrentQuestion();
                calculateScoresForCurrentQuestion();
                List<Player> topTenPlayers =  getTopPlayers(10);
                String topTenPlayersJson = null;
                try {
                    topTenPlayersJson = objectMapper.writeValueAsString(topTenPlayers);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (currentQuestionIndex >= quiz.getQuestions().size() - 1) {
                    state = QuizSessionState.FINISHED;
                    broadcastMessage(MessageType.FINAL_RESULTS, topTenPlayersJson);
                } else {
                    incrementQuestionIndex();
                    broadcastMessage(MessageType.TIME_UP, topTenPlayersJson);
                }
            }
        }, (long) getCurrentQuestion().getTime() * 1000);
    }

    public void closeCurrentQuestion() {
        state = QuizSessionState.WAITING_FOR_NEXT_QUESTION;
    }

    private void messageOwner(MessageType messageType) {
        try {
            ownerSession.getBasicRemote().sendText(messageType.toString());
        } catch (Exception e) {
            System.out.println("Error sending message to owner: " + e.getMessage());
        }
    }


    private void messageOwner(MessageType messageType, String message) {
        String fullMessage = messageType.toString().concat(":").concat(message);
        try {
            ownerSession.getBasicRemote().sendText(fullMessage);
        } catch (Exception e) {
            System.out.println("Error sending message to owner: " + e.getMessage());
        }
    }

    private void messagePlayers(MessageType messageType, String message) {
        String fullMessage = messageType.toString().concat(":").concat(message);
        for (Player player : players) {
            try {
                player.sendMessage(fullMessage);
            } catch (Exception e) {
                System.out.println("Error sending message to player: " + e.getMessage());
            }
        }
    }

    public void calculateScoresForCurrentQuestion() {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion.getCorrectAnswer() == null) {
            return;
        }

        for (Player player : players) {
            Long playerAnswer = player.getAnswerToQuestion(currentQuestion.getId());
            Long correctAnswer = currentQuestion.getCorrectAnswer().getId();
            if (playerAnswer != null && playerAnswer.equals(correctAnswer)) {
                player.incrementScore(currentQuestion.getPoints());
            }
        }
    }

    private List<Player> getTopPlayers(int n) {
        List<Player> topPlayers = new ArrayList<>(players);
        if (topPlayers.size() > 1) {
            topPlayers.sort(Comparator.comparing(Player::getScore).reversed());
        }
        if (topPlayers.size() >= n) {
            return topPlayers.subList(0, n);
        }

        return topPlayers;
    }

    public Question getCurrentQuestion() {
        return quiz.getQuestions().get(currentQuestionIndex);
    }

    public void broadcastCurrentQuestion() {
        // Message question to owner
        String question = null;
        try {
            question = objectMapper.writeValueAsString(QuestionProjection.toProjection(quiz.getQuestions().get(currentQuestionIndex)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageOwner(MessageType.QUESTION, question);

        // Message question to players. Exclude information about correct answer.
        String questionWithoutCorrectAnswer = null;
        try {
            questionWithoutCorrectAnswer = objectMapper.writeValueAsString(PlayerQuestionProjection.toProjection(quiz.getQuestions().get(currentQuestionIndex)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        messagePlayers(MessageType.QUESTION, questionWithoutCorrectAnswer);
    }

    private void broadcastMessage(MessageType messageType, String message) {
        messageOwner(messageType, message);
        messagePlayers(messageType, message);
    }

    public void addPlayer(Player player) {
        if (state == QuizSessionState.WAITING_FOR_PLAYERS) {
            players.add(player);
            broadcastMessage(MessageType.NUMBER_OF_PLAYERS, String.valueOf(players.size()));

            QuizProjection quizProjection = QuizProjection.toProjection(quiz);
            quizProjection.setQuestions(null);
            String quizJson = null;
            try {
                quizJson = objectMapper.writeValueAsString(quizProjection);
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.sendMessage(MessageType.QUIZ_DETAILS + ":" + quizJson);
        }
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        broadcastMessage(MessageType.NUMBER_OF_PLAYERS, String.valueOf(players.size()));
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public void sendResultsAsXls() {
        List<Player> topPlayers = getTopPlayers(players.size());
        String xls = null;
        try {
            xls = ExcelGenerator.generateExcelFile(topPlayers);
        } catch (IOException e) {
            xls = "";
        }
        messageOwner(MessageType.FINAL_RESULTS_AS_XLS, xls);
    }

    private static enum QuizSessionState {
        WAITING_FOR_PLAYERS,
        QUESTION_ACTIVE,
        WAITING_FOR_NEXT_QUESTION,
        FINISHED
    }


    private static enum MessageType {
        NUMBER_OF_PLAYERS,
        QUESTION,
        TIME_UP,
        FINAL_RESULTS,
        FINAL_RESULTS_AS_XLS,
        QUIZ_STARTED,
        QUIZ_DETAILS
    }
}
