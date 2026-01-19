package quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quiz.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query(
            value = "SELECT * FROM movie WHERE year = ?1 ORDER BY RAND() LIMIT 1",
            nativeQuery = true
    )
    Movie findRandomByYear(int year);
}
