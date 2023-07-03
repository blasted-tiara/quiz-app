package ba.fet.rwa.projections;

import ba.fet.rwa.models.Question;

import java.util.List;

public class QuestionProjection {
    private Long id;
    private String text;
    private int time;
    private int points;
    private int questionOrder;
    private List<AnswerProjection> answers;

    public static QuestionProjection toProjection(Question model) {
        QuestionProjection projection = new QuestionProjection();

        projection.setId(model.getId());
        projection.setText(model.getText());
        projection.setTime(model.getTime());
        projection.setPoints(model.getPoints());
        projection.setQuestionOrder(model.getQuestionOrder());
        projection.setAnswers(AnswerProjection.toProjection(model.getAnswers()));

        return projection;
    }

    public static List<QuestionProjection> toProjection(List<Question> models) {
        return models.stream().map(QuestionProjection::toProjection).collect(java.util.stream.Collectors.toList());
    }

    public Question toModel() {
        Question model = new Question();

        model.setId(this.id);
        model.setText(this.text);
        model.setTime(this.time);
        model.setPoints(this.points);
        model.setQuestionOrder(this.questionOrder);
        model.setAnswers(AnswerProjection.toModel(this.answers));

        return model;
    }

    public static List<Question> toModel(List<QuestionProjection> projections) {
        return projections.stream().map(QuestionProjection::toModel).collect(java.util.stream.Collectors.toList());
    }

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

    public List<AnswerProjection> getAnswers() {
        return answers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public void setAnswers(List<AnswerProjection> answers) {
        this.answers = answers;
    }
}
