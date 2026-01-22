package quiz.service;

import jakarta.transaction.Transactional;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import quiz.entity.User;
import quiz.repository.UserRepository;

@Service
public class ProfilePictureService {

    @Value("${profile.picture.path}")
    private String profilePictureDir;

    private final UserRepository userRepository;

    public ProfilePictureService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Path userDir(String username) {
        return Paths.get(profilePictureDir).resolve(username);
    }

    private Path defaultsDir() {
        return Paths.get(profilePictureDir).resolve("defaults");
    }

    public String getRandomProfilePic() {
        Path dir = defaultsDir();

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalStateException("Default profile pictures directory missing: " + dir);
        }

        try (var stream = Files.list(dir)) {
            var files = stream
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .toList();

            if (files.isEmpty()) {
                throw new IllegalStateException("No default profile pictures found.");
            }

            return files.get(new Random().nextInt(files.size()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default profile pictures.", e);
        }
    }




    @Transactional
    public String uploadProfilePicture(String username, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded.");
        }

        String contentType = file.getContentType();
        if (!Objects.equals(contentType, "image/jpeg") && !Objects.equals(contentType, "image/png")) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed.");
        }


        BufferedImage img;
        try (InputStream in = file.getInputStream()) {
            img = ImageIO.read(in);
        }
        if (img == null) {
            throw new IllegalArgumentException("Invalid image file.");
        }

        Path dir = userDir(username);
        Files.createDirectories(dir);

        Path out = dir.resolve("profile.png");
        try (OutputStream os = Files.newOutputStream(out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ImageIO.write(img, "png", os);
        }


        updateUserProfilePicture(username, "profile.png");

        return out.toString();
    }

    @Transactional
    public String updateUserProfilePicture(String username, String pictureFilename) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found.");

        user.setProfilePicture(pictureFilename);
        userRepository.save(user);
        return pictureFilename;
    }

    public Resource loadUserProfilePictureAsResource(String username) throws IOException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found.");

        String pic = user.getProfilePicture();
        if (pic == null || pic.isBlank()) {
            throw new FileNotFoundException("User has no profile picture set.");
        }

        Path file = "profile.png".equals(pic)
                ? userDir(username).resolve("profile.png")
                : defaultsDir().resolve(pic);

        if (!Files.exists(file)) throw new FileNotFoundException("Missing: " + file);

        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("Could not read: " + file);
        }
        return resource;
    }

}
