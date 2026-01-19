package quiz.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.dto.LeaderboardEntry;
import quiz.repository.UserQuizAttemptRepository;

@Service
public class LeaderboardService {
    @Autowired
    private UserQuizAttemptRepository userQuizAttemptRepository;

    public List<LeaderboardEntry> getLeaderboardForAllTime() {
        Date beginning = new Date(0L);
        return this.userQuizAttemptRepository.findLeaderboardEntriesByDate(beginning);
    }

    public List<LeaderboardEntry> getLeaderboardForLast7Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -7);
        Date oneWeekAgo = calendar.getTime();
        return this.userQuizAttemptRepository.findLeaderboardEntriesByDate(oneWeekAgo);
    }

    public List<LeaderboardEntry> getLeaderboardForLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(2, -1);
        Date oneMonthAgo = calendar.getTime();
        return this.userQuizAttemptRepository.findLeaderboardEntriesByDate(oneMonthAgo);
    }
}
