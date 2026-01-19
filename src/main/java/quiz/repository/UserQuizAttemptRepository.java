package quiz.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import quiz.dto.LeaderboardEntry;
import quiz.entity.UserQuizAttempt;

public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {
    @Query("SELECT new quiz.dto.LeaderboardEntry(u.username, SUM(uqa.points), u.profilePicture) FROM UserQuizAttempt uqa JOIN uqa.user u GROUP BY u.username, u.profilePicture ORDER BY SUM(uqa.points) DESC")
    List<LeaderboardEntry> findLeaderboard();

    @Query("SELECT sum(uqa.points) FROM UserQuizAttempt uqa WHERE uqa.user.id = :userId")
    Long sumPointsByUserId(@Param("userId") Long userId);

    @Query("SELECT count(uqa) FROM UserQuizAttempt uqa JOIN uqa.quizItem.newsEvent ne WHERE uqa.guessedYear = ne.year and uqa.user.id = :userId")
    Long countRightGuessesByUserId(@Param("userId") Long userId);

    @Query("SELECT count(distinct uqa.attemptDate) FROM UserQuizAttempt uqa WHERE uqa.user.id = :userId")
    Long countDistinctAttemptDaysByUserId(@Param("userId") Long userId);

    @Query("SELECT new quiz.dto.LeaderboardEntry(u.username, SUM(uqa.points), u.profilePicture) FROM UserQuizAttempt uqa JOIN uqa.user u WHERE uqa.attemptDate >= :startdate GROUP BY u.username, u.profilePicture ORDER BY SUM(uqa.points) DESC")
    List<LeaderboardEntry> findLeaderboardEntriesByDate(@Param("startdate") Date startDate);

    @Query("SELECT COUNT(u) FROM UserQuizAttempt u WHERE u.user.id = :userId AND DATE(u.attemptDate) = CURRENT_DATE")
    Long countTodayAttemptByUserId(@Param("userId") Long userId);
}
