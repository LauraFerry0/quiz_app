package quiz.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import quiz.entity.ConfirmationToken;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
}
