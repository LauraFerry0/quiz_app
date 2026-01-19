package quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quiz.entity.NewsEvent;

@Repository
public interface NewsEventRepository extends JpaRepository<NewsEvent, Long> {
    @Query(
            value = "SELECT * FROM newsevent ORDER BY RAND() LIMIT 1",
            nativeQuery = true
    )
    NewsEvent findRandom();
}
