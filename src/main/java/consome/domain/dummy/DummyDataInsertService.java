package consome.domain.dummy;


import consome.domain.user.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.instancio.Instancio;
import org.springframework.stereotype.Service;

import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
public class DummyDataInsertService {

    private final EntityManagerFactory emf;

    public void bulkInsertDummyData(int count) {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();

        for (int i = 0; i < count; i++) {
            int finalI = i;
            int finalI1 = i;
            User user = Instancio.of(User.class)
                    .supply(field("loginId"), () -> "login-" + finalI)
                    .supply(field("nickname"), () -> "nick" + finalI1)
                    .supply(field("password"), () -> "password123")
                    .supply(field("role"), () -> consome.domain.user.Role.USER)
                    .create();

            session.insert(user);

            if (i % 1000 == 0) {
                System.out.println("Inserted: " + i);
            }
        }
        tx.commit();
        session.close();

    }
}
