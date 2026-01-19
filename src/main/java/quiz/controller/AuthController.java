package quiz.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.dto.UserDTO;
import quiz.entity.User;
import quiz.service.UserService;


@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping({"/login"})
    public String login(@RequestParam(value = "error",required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }

        return "login";
    }

    @GetMapping({"/register"})
    public String register() {
        return "register";
    }

    @PostMapping({"/register"})
    public String registerUser(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            User newUser = this.userService.createUser(
                    userDTO.getEmail(),
                    userDTO.getUsername(),
                    userDTO.getPassword());

            redirectAttributes.addFlashAttribute("successMessage", "Account created! You can log in now");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            logger.error("Error during registration: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}
