package ba.fet.rwa.services;

import ba.fet.rwa.models.Answer;
import ba.fet.rwa.models.Question;
import ba.fet.rwa.models.Quiz;
import ba.fet.rwa.projections.QuizProjection;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizService {
    public static final int PAGE_SIZE = 20;

    private ImageService imageService = new ImageService();

    public Response createQuiz(InputStream thumbnailFile, FormDataContentDisposition thumbnailFileDetail, String title, String description) {
        String fileName;

        try {
            fileName = imageService.createImage(thumbnailFile);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while uploading image").build();
        }

        Quiz quiz;
        try {
            quiz = createQuiz(title, fileName, description);
            return Response.ok(quiz).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while creating quiz").build();
        }
    }

    private static Quiz createQuiz(String title, String thumbnail, String description) {
        // logic for creating new quiz entity
        Transaction tx = null;
        Quiz quiz = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setThumbnail(thumbnail);
            quiz.setDescription(description);

            session.persist(quiz);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        return quiz;
    }

    public Response getQuizById(String id) {
        Transaction tx = null;
        Quiz quiz;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            quiz = session.get(Quiz.class, id);

            if (quiz == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Quiz not found for id: " + id).build();
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while fetching the quiz").build();
        }

        return Response.ok(QuizProjection.toProjection(quiz)).build();
    }

    public Response updateQuiz(String id, InputStream thumbnailFile, FormDataContentDisposition thumbnailFileDetail, QuizProjection projection) {
        Transaction tx = null;
        Quiz quiz;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            quiz = session.get(Quiz.class, id);
            Quiz changedQuiz = projection.toModel();

            if (quiz == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Quiz not found.").build();
            }

            if (thumbnailFile != null) {
                String fileName = imageService.createImage(thumbnailFile);
                quiz.setThumbnail(fileName);
            }

            if (projection.getTitle() != null) {
                quiz.setTitle(projection.getTitle());
            }

            if (projection.getDescription() != null) {
                quiz.setDescription(projection.getDescription());
            }

            List<Question> questionsToRemove = getQuestionsToRemove(quiz.getQuestions(), changedQuiz.getQuestions());
            removeQuestions(questionsToRemove, session);
            quiz.getQuestions().removeAll(questionsToRemove);

            for (Question changedQuestion : changedQuiz.getQuestions()) {
                if (changedQuestion.getId() == null) {
                    createQuestion(changedQuestion, quiz, session);
                } else {
                    Question initialQuestion = session.get(Question.class, changedQuestion.getId());
                    updateQuestion(initialQuestion, changedQuestion, session);
                }
            }

            session.merge(quiz);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while updating the quiz").build();
        }

        return Response.ok(QuizProjection.toProjection(quiz)).build();
    }

    private void updateQuestion(Question initialQuestion, Question changedQuestion, Session session) {
        if (changedQuestion.getText() != null) {
            initialQuestion.setText(changedQuestion.getText());
        }

        initialQuestion.setPoints(changedQuestion.getPoints());
        initialQuestion.setTime(changedQuestion.getTime());

        session.merge(initialQuestion);

        List<Answer> answersToRemove = getAnswersToRemove(initialQuestion.getAnswers(), changedQuestion.getAnswers());
        removeAnswers(answersToRemove, session);
        initialQuestion.getAnswers().removeAll(answersToRemove);

        for (Answer changedAnswer : changedQuestion.getAnswers()) {
            if (changedAnswer.getId() == null) {
                createAnswer(changedAnswer, initialQuestion, session);
            } else {
                Answer initialAnswer = session.get(Answer.class, changedAnswer.getId());
                updateAnswer(initialAnswer, changedAnswer, session);
            }
        }
    }

    private void updateAnswer(Answer initialAnswer, Answer changedAnswer, Session session) {
        if (changedAnswer.getText() != null) {
            initialAnswer.setText(changedAnswer.getText());
        }
        initialAnswer.setCorrect(changedAnswer.isCorrect());

        session.merge(initialAnswer);
    }

    private void createQuestion(Question question, Quiz quiz, Session session) {
        question.setQuiz(quiz);
        for (Answer answer : question.getAnswers()) {
            answer.setQuestion(question);
        }
        session.persist(question);
    }

    private void createAnswer(Answer answer, Question question, Session session) {
        answer.setQuestion(question);
        session.persist(answer);
    }

    private void removeQuestions(List<Question> questionsToRemove, Session session) {
        for (Question question : questionsToRemove) {
            session.remove(question);
        }
    }

    private void removeAnswers(List<Answer> answersToRemove, Session session) {
        for (Answer answer : answersToRemove) {
            session.remove(answer);
        }
    }

    private List<Question> getQuestionsToRemove(List<Question> initialQuestions, List<Question> changedQuestions) {
        List<Question> questionsToRemove = new ArrayList<>();

        for (Question initialQuestion : initialQuestions) {
            if (!containsQuestion(changedQuestions, initialQuestion.getId())) {
                questionsToRemove.add(initialQuestion);
            }
        }

        return questionsToRemove;
    }

    private List<Answer> getAnswersToRemove(List<Answer> initialAnswers, List<Answer> changedAnswers) {
        List<Answer> answersToRemove = new ArrayList<>();

        for (Answer initialAnswer : initialAnswers) {
            if (!containsAnswer(changedAnswers, initialAnswer.getId())) {
                answersToRemove.add(initialAnswer);
            }
        }

        return answersToRemove;
    }

    private boolean containsQuestion(List<Question> questions, Long questionId) {
        return questions.stream().anyMatch(question -> Objects.equals(question.getId(), questionId));
    }

    private boolean containsAnswer(List<Answer> answers, Long answerId) {
        return answers.stream().anyMatch(answer -> Objects.equals(answer.getId(), answerId));
    }

    public Response deleteQuiz(String id) {
        Transaction tx = null;
        Quiz quiz;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            quiz = session.get(Quiz.class, id);

            if (quiz == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Quiz not found.").build();
            }

            session.remove(quiz);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while deleting the quiz").build();
        }

        return Response.ok("Quiz deleted successfully").build();
    }

    public Response getPaginatedQuizzes(Integer page) {
        Transaction transaction = null;
        List<Quiz> listOfQuizzes;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Quiz> query = session.createQuery("FROM Quiz u ORDER BY u.title DESC", Quiz.class);

            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);

            listOfQuizzes = query.getResultList();

            transaction.commit();
            return Response.ok(QuizProjection.toProjection(listOfQuizzes)).build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while fetching the page").build();
    }
}
