package quiz.service;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.entity.QuizItem;
import quiz.entity.User;
import quiz.entity.UserQuizAttempt;
import quiz.repository.UserQuizAttemptRepository;

@Service
public class UserQuizAttemptService {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizAttemptService.class);
    @Autowired
    private UserQuizAttemptRepository userQuizAttemptRepository;

    public UserQuizAttempt saveUserQuizAttempt(User user, QuizItem quizItem, int guessedYear) {
        if (user == null) {
            logger.info("UserQuizAttemptService::saveUserQuizAttempt - User: {}, QuizItem: {}, GuessedYear: {}", new Object[]{user, quizItem, guessedYear});
            logger.error("User object is null. Cannot save quiz attempt.");
            throw new IllegalArgumentException("User cannot be null");
        } else {
            logger.info("Saving user quiz attempt for user: {}", user.getUsername());
            UserQuizAttempt userQuizAttempt = new UserQuizAttempt();
            userQuizAttempt.setUser(user);
            userQuizAttempt.setQuizItem(quizItem);
            userQuizAttempt.setGuessedYear(guessedYear);
            userQuizAttempt.setAttemptDate(new Date());
            int points = this.calculatePoints(quizItem.getYear(), guessedYear);
            userQuizAttempt.setPoints(points);
            UserQuizAttempt savedAttempt = (UserQuizAttempt)this.userQuizAttemptRepository.save(userQuizAttempt);
            logger.info("Saved user quiz attempt with ID: {}", savedAttempt.getId());
            return savedAttempt;
        }
    }

    private int calculatePoints(int correctYear, int guessedYear) {
        int difference = Math.abs(correctYear - guessedYear);
        if (difference == 0) {
            return 10;
        } else if (difference == 1) {
            return 3;
        } else if (difference == 2) {
            return 2;
        } else {
            return difference == 3 ? 1 : 0;
        }
    }

    public boolean hasUserAttemptedQuizToday(User user) {
        assert user != null : "User cannot be null";

        Long attemptsTodayLong = this.userQuizAttemptRepository.countTodayAttemptByUserId(user.getId());
        return attemptsTodayLong != null && attemptsTodayLong > 0L;
    }
}
