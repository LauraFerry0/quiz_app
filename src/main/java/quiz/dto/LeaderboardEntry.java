package quiz.dto;

public class LeaderboardEntry {
    private String username;
    private Long totalPoints;
    private String profilePicture;

    public LeaderboardEntry(String username, Long totalPoints, String profilePicture) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTotalPoints() {
        return this.totalPoints;
    }

    public void setTotalPoints(Long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
