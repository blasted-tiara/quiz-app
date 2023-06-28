package ba.fet.rwa.services;

import ba.fet.rwa.models.Quiz;
import ba.fet.rwa.projections.QuizProjection;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class QuizService {
    public static final int PAGE_SIZE = 20;
    public static final String UPLOAD_DIR_NAME = "images";

    public Response createQuiz(InputStream thumbnailFile, FormDataContentDisposition thumbnailFileDetail, String title, String description) {
        String fileName = null;

        if (thumbnailFileDetail != null) {
            try {
                File uploadDir = new File(UPLOAD_DIR_NAME);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                fileName = UUID.randomUUID().toString();
                Files.copy(thumbnailFile, Paths.get(uploadDir.getPath(), fileName));
            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }

        return Response.ok(createQuiz(title, fileName, description)).build();
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

        return Response.ok(quiz).build();
    }

    public Response updateQuiz(String id, InputStream thumbnailFile, FormDataContentDisposition thumbnailFileDetail, String title, String description) {
        return null;
    }

    public Response deleteQuiz(String id) {
        return null;
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
