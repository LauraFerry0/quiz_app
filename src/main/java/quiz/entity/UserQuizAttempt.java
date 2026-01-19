package quiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;

@Entity
public class UserQuizAttempt {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "quiz_item_id",
            nullable = false
    )
    private QuizItem quizItem;
    @Column(
            nullable = false
    )
    private Date attemptDate;
    private int guessedYear;
    private int points;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public QuizItem getQuizItem() {
        return this.quizItem;
    }

    public void setQuizItem(QuizItem quizItem) {
        this.quizItem = quizItem;
    }

    public Date getAttemptDate() {
        return this.attemptDate;
    }

    public void setAttemptDate(Date attemptDate) {
        this.attemptDate = attemptDate;
    }

    public int getGuessedYear() {
        return this.guessedYear;
    }

    public void setGuessedYear(int guessedYear) {
        this.guessedYear = guessedYear;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
