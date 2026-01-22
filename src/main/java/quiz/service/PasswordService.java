package quiz.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import quiz.entity.User;
import quiz.repository.UserRepository;

@Service
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    @Value("${app.base.url}")
    private String appBaseUrl;

    public PasswordService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ConfirmationTokenService confirmationTokenService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    @Transactional
    public void changePassword(String identifier, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(identifier);
        if (user == null) user = userRepository.findByEmail(identifier);
        if (user == null) throw new IllegalArgumentException("User not found");

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return;

        String token = confirmationTokenService.createToken(user);
        String resetLink = appBaseUrl + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = confirmationTokenService.validateToken(token);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        confirmationTokenService.markUsed(token);
    }

    public void validateResetToken(String token) {
        confirmationTokenService.validateToken(token);
    }
}

