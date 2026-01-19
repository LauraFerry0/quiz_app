package quiz.service;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.entity.ConfirmationToken;
import quiz.entity.User;
import quiz.repository.ConfirmationTokenRepository;

@Service
public class ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                user,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );

        this.confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    public User validateToken(String token) {
        ConfirmationToken ct = confirmationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (ct.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        } else {
            return ct.getUser();
        }
    }

    @Transactional
    public void markUsed(String token) {
        ConfirmationToken ct = confirmationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));

        if (ct.getConfirmedAt() !=null) {
            throw new IllegalStateException("Token expired");
        }

        ct.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(ct);
    }

}
