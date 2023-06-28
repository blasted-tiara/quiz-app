package ba.fet.rwa.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.validation.Valid;
import javax.ws.rs.core.Response;

public class PageService {
    public <T> Response getPagesCountResponse(int pageSize, Class<T> entityClass) {
        PagesInfo pagesInfo;
        try {
            pagesInfo = getPagesCount(pageSize, entityClass);
            return Response.ok(pagesInfo).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public <T> PagesInfo getPagesCount(int pageSize, Class<T> entityClass) {
        Transaction tx = null;
        int pagesCount = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String entityName = entityClass.getSimpleName();
            Query<Long> query = session.createQuery("select count(*) from " + entityName, Long.class);
            Long numberOfEntries = query.uniqueResult();
            pagesCount = (int) Math.ceil(numberOfEntries.doubleValue() / pageSize);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return new PagesInfo(pagesCount);
    }

    public static class PagesInfo {
        public PagesInfo(Integer pagesCount) {
            this.pagesCount = pagesCount;
        }

        private @Valid Integer pagesCount;

        @JsonProperty("pagesCount")
        public Integer getPagesCount() {
            return pagesCount;
        }

        public void setPagesCount(Integer pagesCount) {
            this.pagesCount = pagesCount;
        }
    }
}
