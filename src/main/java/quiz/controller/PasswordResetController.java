package quiz.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.service.PasswordService;

@Controller
public class PasswordResetController {

    private final PasswordService passwordService;

    public PasswordResetController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // =========================================================
    // FORGOT PASSWORD (LOGGED OUT FLOW)
    // =========================================================

    /**
     * Displays the "Forgot Password" page.
     * This page lets a logged-out user enter their email address
     * to request a password reset link.
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    /**
     * Handles submission of the "Forgot Password" form.
     * If the email exists, a reset token is generated and emailed.
     * Always returns a generic message to avoid leaking user existence.
     */
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            passwordService.requestPasswordReset(email);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "If an account exists for that email, reset instructions have been sent."
            );
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to send password reset email. Please try again later."
            );
            return "redirect:/forgot-password";
        }
    }

    // =========================================================
    // RESET PASSWORD (EMAIL TOKEN FLOW)
    // =========================================================

    /**
     * Displays the "Reset Password" page after the user clicks
     * the link in their email.
     * Requires a valid reset token.
     * If the token is missing or invalid, redirects back to
     * the Forgot Password page.
     */
    @GetMapping("/reset-password")
    public String showResetPasswordPage(
            @RequestParam(value = "token", required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Use Forgot Password to get a reset link."
            );
            return "redirect:/forgot-password";
        }

        try {
            passwordService.validateResetToken(token);
            model.addAttribute("token", token);
            return "reset-password";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Invalid or expired reset link."
            );
            return "redirect:/forgot-password";
        }
    }

    /**
     * Handles submission of the Reset Password form.
     * Uses the reset token to identify the user and update
     * their password.
     */
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Passwords do not match."
            );
            return "redirect:/reset-password?token=" + token;
        }

        try {
            passwordService.resetPassword(token, newPassword);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Password has been reset successfully. You can now log in."
            );
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Invalid or expired reset link."
            );
            return "redirect:/forgot-password";
        }
    }

    // =========================================================
    // CHANGE PASSWORD (LOGGED IN FLOW)
    // =========================================================

    /**
     * Displays the Change Password page for a logged-in user.
     */
    @GetMapping("/settings/change-password")
    public String showChangePasswordPage(Principal principal) {
        if (principal == null) return "redirect:/login";
        return "change-password";
    }

    /**
     * Handles submission of the Change Password form.
     * Requires the user's current password for verification.
     */
    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Passwords do not match."
            );
            return "redirect:/settings/change-password";
        }

        try {
            passwordService.changePassword(
                    principal.getName(),
                    currentPassword,
                    newPassword
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Password updated."
            );
            return "redirect:/settings";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
            return "redirect:/settings/change-password";
        }
    }
}
