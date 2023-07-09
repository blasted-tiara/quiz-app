package ba.fet.rwa.models;

import ba.fet.rwa.services.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
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
        startCurrentQuestion();
    }

    private void incrementQuestionIndex() {
        currentQuestionIndex++;
    }

    public void startCurrentQuestion() {
        if (currentQuestionIndex == -1) {
            throw new RuntimeException("Quiz not started");
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
                broadcastMessage(MessageType.SCORES ,topTenPlayersJson);
                if (currentQuestionIndex < quiz.getQuestions().size() - 1) {
                    incrementQuestionIndex();
                } else {
                    state = QuizSessionState.FINISHED;
                    broadcastMessage(MessageType.QUIZ_FINISHED, topTenPlayersJson);
                }
            }
        }, (long) getCurrentQuestion().getTime() * 1000);
    }

    public void closeCurrentQuestion() {
        state = QuizSessionState.WAITING_FOR_NEXT_QUESTION;
    }

    private void messageOwner(MessageType messageType, String message) {
        String fullMessage = messageType.toString().concat(":").concat(message);
        try {
            ownerSession.getBasicRemote().sendText(fullMessage);
        } catch (Exception e) {
            System.out.println("Error sending message to owner: " + e.getMessage());
        }
    }

    public void calculateScoresForCurrentQuestion() {
        Question currentQuestion = getCurrentQuestion();
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
        topPlayers.sort(Comparator.comparing(Player::getScore).reversed());
        return topPlayers.subList(0, n);
    }

    private Question getCurrentQuestion() {
        return quiz.getQuestions().get(currentQuestionIndex);
    }

    public void broadcastCurrentQuestion() {
        String question = null;
        try {
            question = objectMapper.writeValueAsString(quiz.getQuestions().get(currentQuestionIndex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        broadcastMessage(MessageType.QUESTION ,question);
    }

    private void broadcastMessage(MessageType messageType, String message) {
        for (Player player : players) {
            player.sendMessage(messageType.toString().concat(":").concat(message));
        }
    }

    public void addPlayer(Player player) {
        if (state == QuizSessionState.WAITING_FOR_PLAYERS) {
            players.add(player);
            messageOwner(MessageType.NUMBER_OF_PLAYERS, String.valueOf(players.size()));
        }
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        messageOwner(MessageType.NUMBER_OF_PLAYERS, String.valueOf(players.size()));
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
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
        SCORES,
        QUIZ_FINISHED
    }
}