package booking.service.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import booking.service.model.User;
import booking.service.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(scripts = {
            "classpath:database/delete-users-and-roles.sql",
            "classpath:database/insert-users-and-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-users-and-roles.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindByEmail() {
        Optional<User> user = userRepository.findByEmail("test@example.com");
        assertTrue(user.isPresent());
    }

    @Test
    @Sql(scripts = {
            "classpath:database/delete-users-and-roles.sql",
            "classpath:database/insert-users-and-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-users-and-roles.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertTrue(exists);
    }
}

