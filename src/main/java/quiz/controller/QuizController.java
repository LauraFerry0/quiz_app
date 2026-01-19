package quiz.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.entity.QuizItem;
import quiz.entity.User;
import quiz.service.QuizItemService;
import quiz.service.UserQuizAttemptService;
import quiz.service.UserService;

@Controller
@RequestMapping({"/quiz"})
public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    @Autowired
    private QuizItemService quizService;
    @Autowired
    private UserQuizAttemptService userQuizAttemptService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String getQuizPage(Model model, Principal principal) {
        if (principal == null) {
            logger.error("User Not Logged In");
            return "redirect:/login";
        } else {
            String username = principal.getName();
            User user = this.userService.findByUsername(username);
            if (user == null) {
                logger.error("User not logged in");
                return "redirect:/login";
            } else if (this.userQuizAttemptService.hasUserAttemptedQuizToday(user)) {
                model.addAttribute("message", "You have already attempted today's quiz.");
                return "redirect:/leaderboard";
            } else {
                QuizItem quizItem = this.quizService.getDailyQuizItem();
                model.addAttribute("quizItem", quizItem);
                model.addAttribute("timeLeft", 30);
                model.addAttribute("canSubmit", true);
                return "quiz";
            }
        }
    }

    @PostMapping({"/submit"})
    public String submitQuiz(@RequestParam int guessedYear, @RequestParam Long quizItemId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = principal.toString();
            }
        }

        if (username == null) {
            logger.error("User not logged in");
            return "redirect:/login";
        } else {
            User user = this.userService.findByUsername(username);
            if (user == null) {
                logger.error("User not found in database");
                return "redirect:/login";
            } else {
                QuizItem quizItem = this.quizService.getQuizItemById(quizItemId);
                this.userQuizAttemptService.saveUserQuizAttempt(user, quizItem, guessedYear);
                int actualYear = quizItem.getYear();
                int diff = Math.abs(actualYear - guessedYear);
                String message;
                if (diff == 0) {
                    message = "Congrats! You guessed the exact year. 10 points to you!";
                } else if (diff <= 1) {
                    message = "Close! The correct year is " + actualYear + ". But you get 3 points.";
                } else if (diff <= 2) {
                    message = "Not bad! You're close. The correct year is " + actualYear + ". Only 2 points.";
                } else if (diff <= 3) {
                    message = "The correct year is " + actualYear + ". Only 1 point.";
                } else {
                    message = "I feel sorry for you, your guess is off by " + diff + " years. The correct year is " + actualYear + ".";
                }

                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/leaderboard";
            }
        }
    }
}
