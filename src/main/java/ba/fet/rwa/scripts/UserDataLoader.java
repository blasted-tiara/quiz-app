package ba.fet.rwa.scripts;

import ba.fet.rwa.models.User;
import ba.fet.rwa.services.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

public class UserDataLoader {
    public static void main(String[] args) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Create new user object
            User user = new User();
            user.setUsername("admin");
            String passwordHash = BCrypt.hashpw("password", BCrypt.gensalt());
            user.setPasswordHash(passwordHash);
            Set<User.Role> roles = new HashSet<>();
            roles.add(User.Role.ADMIN);
            user.setRoles(roles);

            // Save user to database
            session.persist(user);

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
