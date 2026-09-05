package equipmentrental.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String name, String role) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Welcome to Agricultural Equipment Rental System");

        String emailBody =
                "Hello " + name + ",\n\n" +
                "Welcome to the Agricultural Equipment Rental System!\n\n" +
                "Your account has been successfully created.\n\n" +
                "Account Details:\n" +
                "Name: " + name + "\n" +
                "Role: " + role + "\n" +
                "Email: " + toEmail + "\n\n" +
                "You can now login and use our platform.\n\n" +
                "Thank you for joining us!\n\n" +
                "Regards,\n" +
                "Agricultural Equipment Rental Team";

        message.setText(emailBody);

        mailSender.send(message);
    }
}