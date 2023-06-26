package ba.fet.rwa.projections;

import ba.fet.rwa.models.Answer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class AnswerProjection {
    private Long id;
    private String text;
    private boolean correct;
    private int order;

    public static AnswerProjection toProjection(Answer model) {
        AnswerProjection projection = new AnswerProjection();

        projection.setId(model.getId());
        projection.setText(model.getText());
        projection.setCorrect(model.isCorrect());
        projection.setOrder(model.getOrder());

        return projection;
    }

    public static List<AnswerProjection> toProjection(List<Answer> models) {
        return models.stream().map(AnswerProjection::toProjection).collect(Collectors.toList());
    }

    public Answer toModel() {
        Answer model = new Answer();

        model.setText(this.text);
        model.setCorrect(this.correct);
        model.setOrder(this.order);

        return model;
    }

    public static List<Answer> toModel(List<AnswerProjection> projections) {
        return projections.stream().map(AnswerProjection::toModel).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getOrder() {
        return order;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("correct")
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @JsonProperty("order")
    public void setOrder(int order) {
        this.order = order;
    }
}
