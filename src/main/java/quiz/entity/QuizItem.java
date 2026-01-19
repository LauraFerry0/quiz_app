package quiz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.util.Date;

@Entity
public class QuizItem {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "news_event_id"
    )
    private NewsEvent newsEvent;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "movie_id"
    )
    private Movie movie;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "song_id"
    )
    private Song song;
    private Date creationDate;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return this.movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public NewsEvent getNewsEvent() {
        return this.newsEvent;
    }

    public void setNewsEvent(NewsEvent newsEvent) {
        this.newsEvent = newsEvent;
    }

    public Song getSong() {
        return this.song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Transient
    public int getYear() {
        if (this.newsEvent != null) {
            return this.newsEvent.getYear();
        } else if (this.movie != null) {
            return this.movie.getYear();
        } else if (this.song != null) {
            return this.song.getYear();
        } else {
            throw new IllegalStateException("No valid year source in QuizItem.");
        }
    }

    public void setYear(int year) {
    }
}
