package ba.fet.rwa.projections;

import ba.fet.rwa.models.Quiz;

import java.util.List;

public class QuizProjection {
    private Long id;
    private String title;
    private String description;
    private String thumbnail;
    private List<QuestionProjection> questions;

    public static QuizProjection toProjection(Quiz model) {
        QuizProjection projection = new QuizProjection();

        projection.setId(model.getId());
        projection.setTitle(model.getTitle());
        projection.setDescription(model.getDescription());
        projection.setThumbnail(model.getThumbnail());
        projection.setQuestions(QuestionProjection.toProjection(model.getQuestions()));

        return projection;
    }

    public static List<QuizProjection> toProjection(List<Quiz> models) {
        return models.stream().map(QuizProjection::toProjection).collect(java.util.stream.Collectors.toList());
    }

    public Quiz toModel() {
        Quiz model = new Quiz();

        model.setId(this.id);
        model.setTitle(this.title);
        model.setDescription(this.description);
        model.setThumbnail(this.thumbnail);
        model.setQuestions(QuestionProjection.toModel(this.questions));

        return model;
    }

    public static List<Quiz> toModel(List<QuizProjection> projections) {
        return projections.stream().map(QuizProjection::toModel).collect(java.util.stream.Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public List<QuestionProjection> getQuestions() {
        return questions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setQuestions(List<QuestionProjection> questions) {
        this.questions = questions;
    }
}
