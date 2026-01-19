package quiz.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String resetLink) {
        try {
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - In This Year Quiz");
            helper.setFrom("in.this.year.quiz@gmail.com");

            String emailContent =
                    "<p>Hi " + escape(username) + ",</p>" +
                            "<p>We received a request to reset your password for your <strong>In This Year Quiz</strong> account.</p>" +
                            "<p>Click the link below to reset your password:</p>" +
                            "<p><a href=\"" + resetLink + "\">Reset your password</a></p>" +
                            "<p>If you did not request a password reset, you can ignore this email.</p>" +
                            "<br><p>Best,<br>The In This Year Quiz Team</p>";

            helper.setText(emailContent, true);
            mailSender.send(email);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
