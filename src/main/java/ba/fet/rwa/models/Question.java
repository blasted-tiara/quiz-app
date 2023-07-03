package ba.fet.rwa.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    private int time;

    private int points;

    private int questionOrder;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Answer> answers;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getTime() {
        return time;
    }

    public int getPoints() {
        return points;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("time")
    public void setTime(int time) {
        this.time = time;
    }

    @JsonProperty("points")
    public void setPoints(int points) {
        this.points = points;
    }

    @JsonProperty("questionOrder")
    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @JsonProperty("answers")
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
