package ba.fet.rwa.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String id;
    private String name;
    private String surname;
    private Map<Long, List<Long>> answers;

    public Player(String id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        answers = new HashMap<>();
    }

    public Player() {
        answers = new HashMap<>();
    }

    public void addAnswer(Long questionId, List<Long> answers) {
        this.answers.put(questionId, answers);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
