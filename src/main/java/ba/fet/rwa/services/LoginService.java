package ba.fet.rwa.services;

import ba.fet.rwa.models.LoginRequest;
import ba.fet.rwa.models.User;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class LoginService {
    public boolean authenticate(LoginRequest loginRequest) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            TypedQuery<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", loginRequest.getUsername());

            List<?> list = query.getResultList();

            transaction.commit();

            if (!list.isEmpty()) {
                User user = (User) list.get(0);
                return BCrypt.checkpw(loginRequest.getPassword(), user.getPasswordHash());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return false;
    }
}
