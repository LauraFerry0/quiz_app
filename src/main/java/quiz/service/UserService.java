package quiz.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import quiz.entity.User;
import quiz.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfilePictureService profilePictureService;

    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ProfilePictureService profilePictureService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profilePictureService = profilePictureService;
    }

    public User findByUsername(String username) { return userRepository.findByUsername(username); }
    public User findByEmail(String email) { return userRepository.findByEmail(email); }

    @Transactional
    public User createUser(String email, String username, String password) {

        log.debug("Attempting registration with user email={} username={}", email, username);

        if (userRepository.findByEmail(email) != null) {
            log.warn("Registration failed: email already exists [{}]", email);
            throw new IllegalArgumentException("A user already exists with this email");

        }

        if (userRepository.findByUsername(username) != null) {
            log.warn("Registration failed: username already exists [{}]", username);
            throw new IllegalArgumentException("A user already exists with this username");
        }

        User user = new User(email, username, passwordEncoder.encode(password));
        user.setProfilePicture(profilePictureService.getRandomProfilePic());

        userRepository.save(user);
        return user;
    }
}

