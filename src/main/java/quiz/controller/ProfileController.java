package quiz.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import quiz.dto.UserProfileStats;
import quiz.entity.User;
import quiz.service.ProfileService;

@Controller
@RequestMapping({"/profile"})
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String getUserProfile(Model model, Principal principal) {
        User user = this.profileService.getUserProfile(principal.getName());
        UserProfileStats stats = this.profileService.getUserProfileStats(principal.getName());
        model.addAttribute("profile", user);
        model.addAttribute("stats", stats);
        return "profile";
    }
}
