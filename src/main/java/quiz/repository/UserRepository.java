package quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quiz.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByUsername(String username);
}
