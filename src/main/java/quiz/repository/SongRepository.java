package quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quiz.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    @Query(
            value = "SELECT * FROM music WHERE year = ?1 ORDER BY RAND() LIMIT 1",
            nativeQuery = true
    )
    Song findRandomByYear(int year);
}
