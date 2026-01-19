package quiz.dto;

public class UserProfileStats {
    private int totalPoints;
    private int totalRightGuesses;
    private int totalDaysAttemptedQuiz;

    public UserProfileStats() {
    }

    public UserProfileStats(int totalPoints, int totalRightGuesses, int totalDaysAttemptedQuiz) {
        this.totalPoints = totalPoints;
        this.totalRightGuesses = totalRightGuesses;
        this.totalDaysAttemptedQuiz = totalDaysAttemptedQuiz;
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getTotalRightGuesses() {
        return this.totalRightGuesses;
    }

    public void setTotalRightGuesses(int totalRightGuesses) {
        this.totalRightGuesses = totalRightGuesses;
    }

    public int getTotalDaysAttemptedQuiz() {
        return this.totalDaysAttemptedQuiz;
    }

    public void setTotalDaysAttemptedQuiz(int totalDaysAttemptedQuiz) {
        this.totalDaysAttemptedQuiz = totalDaysAttemptedQuiz;
    }
}
