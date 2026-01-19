package quiz.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.entity.Movie;
import quiz.entity.NewsEvent;
import quiz.entity.QuizItem;
import quiz.entity.Song;
import quiz.repository.MovieRepository;
import quiz.repository.NewsEventRepository;
import quiz.repository.QuizItemRepository;
import quiz.repository.SongRepository;

@Service
public class QuizItemService {
    private static final Logger logger = LoggerFactory.getLogger(QuizItemService.class);
    private final NewsEventRepository newsEventRepository;
    private final MovieRepository movieRepository;
    private final SongRepository songRepository;
    private final QuizItemRepository quizItemRepository;

    @Autowired
    public QuizItemService(NewsEventRepository newsEventRepository, MovieRepository movieRepository, SongRepository songRepository, QuizItemRepository quizItemRepository) {
        this.newsEventRepository = newsEventRepository;
        this.movieRepository = movieRepository;
        this.songRepository = songRepository;
        this.quizItemRepository = quizItemRepository;
    }

    public QuizItem getDailyQuizItem() {
        Date today = this.getStartOfDay(new Date());
        logger.info("Fetching quiz item for date: {}", today);
        Optional<QuizItem> existingQuizItem = this.quizItemRepository.findFirstByCreationDateOrderByCreationDateDesc(today);
        if (existingQuizItem.isPresent()) {
            logger.info("Quiz item found for today: {}", existingQuizItem.get());
            return (QuizItem)existingQuizItem.get();
        } else {
            logger.info("No quiz item found for today, creating a new one.");
            QuizItem newQuizItem = this.createQuizItem();
            newQuizItem.setCreationDate(today);
            return (QuizItem)this.quizItemRepository.save(newQuizItem);
        }
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public QuizItem createQuizItem() {
        NewsEvent newsEvent = this.newsEventRepository.findRandom();
        if (newsEvent == null) {
            logger.error("No NewsEvent found!");
            throw new RuntimeException("No NewsEvent found!");
        } else {
            logger.info("NewsEvent found: {}", newsEvent);
            Movie movie = this.movieRepository.findRandomByYear(newsEvent.getYear());
            if (movie == null) {
                logger.error("No Movie found for year: {}", newsEvent.getYear());
                throw new RuntimeException("No Movie found for year: " + newsEvent.getYear());
            } else {
                logger.info("Movie found: {}", movie);
                Song song = this.songRepository.findRandomByYear(newsEvent.getYear());
                if (song == null) {
                    logger.error("No Song found for year: {}", newsEvent.getYear());
                    throw new RuntimeException("No Song found for year: " + newsEvent.getYear());
                } else {
                    logger.info("Song found: {}", song);
                    QuizItem quizItem = new QuizItem();
                    quizItem.setNewsEvent(newsEvent);
                    quizItem.setMovie(movie);
                    quizItem.setSong(song);
                    if (movie.getYear() != 0 && song.getYear() != 0 && newsEvent.getYear() != 0) {
                        return quizItem;
                    } else {
                        logger.error("One of the entities has year 0! Movie year: {}, Song year: {}, NewsEvent year: {}", new Object[]{movie.getYear(), song.getYear(), newsEvent.getYear()});
                        throw new RuntimeException("One of the entities has year 0!");
                    }
                }
            }
        }
    }

    public QuizItem getQuizItemById(Long id) {
        return (QuizItem)this.quizItemRepository.findById(id).orElseThrow(() -> new RuntimeException("QuizItem not found with id: " + id));
    }
}
