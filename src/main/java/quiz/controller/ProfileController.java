package quiz.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        String username = principal.getName();

        User user = this.profileService.getUserProfile(username);
        UserProfileStats stats = this.profileService.getUserProfileStats(username);

        model.addAttribute("profile", user);
        model.addAttribute("stats", stats);
        model.addAttribute("isSelf", true);
        return "profile";
    }

    @GetMapping("/{username}")
    public String getUserProfileByUsername(@PathVariable String username,
                                           Model model,
                                           Principal principal) {

        User user = profileService.getUserProfile(username);
        UserProfileStats stats = profileService.getUserProfileStats(username);

        boolean isSelf = (principal != null && principal.getName().equalsIgnoreCase(username));

        model.addAttribute("profile", user);
        model.addAttribute("stats", stats);
        model.addAttribute("isSelf", isSelf);

        return "profile";
    }



}
