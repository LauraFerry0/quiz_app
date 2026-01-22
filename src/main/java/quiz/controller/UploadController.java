package quiz.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import quiz.service.ProfilePictureService;

@Controller
@RequestMapping("/settings")
public class UploadController {

    private final ProfilePictureService profilePictureService;

    public UploadController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }

    @GetMapping("")
    public String settingsPage(Principal principal) {
        return (principal == null) ? "redirect:/login" : "settings";
    }

    @PostMapping(value = "/profile-picture", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "message", "Not authenticated"));
        }

        try {
            validateImage(file);
            profilePictureService.uploadProfilePicture(principal.getName(), file);


            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "url", "/profile-pics/me?v=" + System.currentTimeMillis()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("ok", false, "message", "Upload failed"));
    }

}

    private static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("No file uploaded.");

        long maxBytes = 2 * 1024 * 1024;
        if (file.getSize() > maxBytes) throw new IllegalArgumentException("File too large (max 2MB).");

        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/png") || ct.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Only PNG or JPG images allowed.");
        }
    }
}

