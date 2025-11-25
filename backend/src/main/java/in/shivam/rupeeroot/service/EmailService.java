package in.shivam.rupeeroot.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.addAttachment(filename, new ByteArrayResource(attachment));
        mailSender.send(message);
    }

    // --- NEW METHOD: Send Group Invitation ---
    public void sendGroupInvitation(String toEmail, String inviterName, String groupCode) {
        String subject = "Join my Family Group on Money Manager";
        String body = "Hello!\n\n" +
                inviterName + " has invited you to join their expense tracking group.\n\n" +
                "Use this Group Code to join: " + groupCode + "\n\n" +
                "1. Log in to the app.\n" +
                "2. Click the Group icon in the top menu.\n" +
                "3. Select 'Join Group' and enter the code above.\n\n" +
                "Happy Budgeting!";
        sendEmail(toEmail, subject, body);
    }
}