package quiz.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import quiz.service.ProfilePictureService;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    public ProfilePictureController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }

    // Serve the logged-in user's current picture
    @GetMapping("/profile-pics/me")
    public ResponseEntity<Resource> myProfilePic(Principal principal) throws IOException {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Resource file = profilePictureService.loadUserProfilePictureAsResource(principal.getName());

        return ResponseEntity.ok()
                .contentType(guessMediaType(file))
                .cacheControl(CacheControl.noStore())
                .body(file);
    }

    private static MediaType guessMediaType(Resource resource) {
        String name = resource.getFilename();
        if (name == null) return MediaType.APPLICATION_OCTET_STREAM;

        String lower = name.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}

