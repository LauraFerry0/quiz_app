package quiz.repository;

import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.entity.QuizItem;

@Repository
public interface QuizItemRepository extends JpaRepository<QuizItem, Long> {
    Optional<QuizItem> findFirstByCreationDateOrderByCreationDateDesc(Date creationDate);
}
