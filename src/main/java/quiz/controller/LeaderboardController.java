package quiz.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import quiz.dto.LeaderboardEntry;
import quiz.service.LeaderboardService;

@Controller
@RequestMapping({"/leaderboard"})
public class LeaderboardController {
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public String getLeaderboard(@RequestParam(value = "period",required = false,defaultValue = "all") String period, Model model, @ModelAttribute("message") String message) {
        List var10000;
        switch (period) {
            case "7" -> var10000 = this.leaderboardService.getLeaderboardForLast7Days();
            case "30" -> var10000 = this.leaderboardService.getLeaderboardForLastMonth();
            default -> var10000 = this.leaderboardService.getLeaderboardForAllTime();
        }

        List<LeaderboardEntry> leaderboard = var10000;
        model.addAttribute("leaderboard", leaderboard);
        model.addAttribute("period", period);
        model.addAttribute("message", message);
        return "leaderboard";
    }
}
