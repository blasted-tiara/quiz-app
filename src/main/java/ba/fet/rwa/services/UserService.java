package ba.fet.rwa.services;

import ba.fet.rwa.models.PagesInfo;
import ba.fet.rwa.models.User;
import ba.fet.rwa.projections.UserCredentialsProjection;
import ba.fet.rwa.projections.UserProjection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;

public class UserService {
    private static final int PAGE_SIZE = 20;

    public Response getUserById(String id) {
        Transaction tx = null;
        User user;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            user = session.get(User.class, id);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found for id: " + id).build();
            } else {
                return Response.ok(UserProjection.toProjection(user)).build();
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while fetching the user").build();
        }
    }

    public User getUserByUsername(String username) {
        Transaction tx = null;
        User user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            List<User> users = query.getResultList();

            if (!users.isEmpty()) {
                user = users.get(0);
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return user;
    }

    public PagesInfo getPagesCount() {
        Transaction tx = null;
        int pagesCount = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Query<Long> query = session.createQuery("select count(*) from User", Long.class);
            Long numberOfEntries = query.uniqueResult();
            pagesCount = (int) Math.ceil(numberOfEntries.doubleValue() / PAGE_SIZE);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return new PagesInfo(pagesCount);
    }

    public Response getPaginatedUsers(Integer page) {
        Transaction transaction = null;
        List<User> listOfUsers;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery("FROM User u ORDER BY u.username DESC", User.class);

            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);

            listOfUsers = query.getResultList();

            transaction.commit();
            return Response.ok(UserProjection.toProjection(listOfUsers)).build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while fetching the page").build();
    }

    public Response createUser(UserCredentialsProjection userProjection) {
        Transaction tx = null;
        User user;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            user = userProjection.toModel();
            session.persist(user);

            tx.commit();

            return Response.status(Response.Status.CREATED).entity(user.toProjection()).build();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while creating the user").build();
    }

    public Response updateUser(String id, UserCredentialsProjection userProjection) {
        Transaction tx = null;
        User user;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            user = session.get(User.class, id);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found for id: " + id).build();
            }

            user.setUsername(userProjection.getUsername());
            user.setPasswordHashFromPlaintext(userProjection.getPassword());
            user.setRoles(new HashSet<>(userProjection.getRoles()));

            session.persist(user);
            tx.commit();

            return Response.ok(user.toProjection()).build();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while updating the user").build();
    }

    public Response removeUserById(String id) {
        Transaction tx = null;
        User user;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            user = session.get(User.class, id);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found for id: " + id).build();
            }

            session.remove(user);
            tx.commit();

            return Response.ok("User successfully deleted.").build();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while fetching the user").build();
    }
}
