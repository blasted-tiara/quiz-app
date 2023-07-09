package ba.fet.rwa.models;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private Session session;
    private String name;
    private String surname;
    private Map<Long, Long> answers;
    private int score = 0;

    public Player(Session session) {
        answers = new HashMap<>();
        this.session = session;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int points) {
        score += points;
    }

    public Long getAnswerToQuestion(Long questionId) {
        return answers.get(questionId);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            System.out.println("Error sending message to player: " + e.getMessage());
        }
    }

    public Player() {
        answers = new HashMap<>();
    }

    public void addAnswer(Long questionId, Long answer) {
        this.answers.put(questionId, answer);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
