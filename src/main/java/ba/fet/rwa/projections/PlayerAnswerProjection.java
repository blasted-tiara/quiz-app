package ba.fet.rwa.projections;

import ba.fet.rwa.models.Answer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerAnswerProjection {
    private Long id;
    private String text;

    public static PlayerAnswerProjection toProjection(Answer model) {
        PlayerAnswerProjection projection = new PlayerAnswerProjection();

        projection.setId(model.getId());
        projection.setText(model.getText());

        return projection;
    }

    public static List<PlayerAnswerProjection> toProjection(List<Answer> models) {
        return models.stream().map(PlayerAnswerProjection::toProjection).collect(Collectors.toList());
    }

    public Answer toModel() {
        Answer model = new Answer();

        model.setId(this.id);
        model.setText(this.text);

        return model;
    }

    public static List<Answer> toModel(List<PlayerAnswerProjection> projections) {
        return projections.stream().map(PlayerAnswerProjection::toModel).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }
}
