package quiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.dto.UserProfileStats;
import quiz.entity.User;
import quiz.repository.UserQuizAttemptRepository;
import quiz.repository.UserRepository;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;

    @Autowired
    public ProfileService(UserRepository userRepository, UserQuizAttemptRepository userQuizAttemptRepository) {
        this.userRepository = userRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
    }

    public User getUserProfile(String username) {
        return this.userRepository.findByUsername(username);
    }

    public UserProfileStats getUserProfileStats(String username) {
        User user = this.userRepository.findByUsername(username);
        Long userId = user.getId();
        Long totalPointsLong = this.userQuizAttemptRepository.sumPointsByUserId(userId);
        int totalPoints = totalPointsLong != null ? totalPointsLong.intValue() : 0;
        Long totalRightGuessesLong = this.userQuizAttemptRepository.countRightGuessesByUserId(userId);
        int totalRightGuesses = totalRightGuessesLong != null ? totalRightGuessesLong.intValue() : 0;
        Long totalDaysAttemptedQuizLong = this.userQuizAttemptRepository.countDistinctAttemptDaysByUserId(userId);
        int totalDaysAttemptedQuiz = totalDaysAttemptedQuizLong != null ? totalDaysAttemptedQuizLong.intValue() : 0;
        return new UserProfileStats(totalPoints, totalRightGuesses, totalDaysAttemptedQuiz);
    }
}
