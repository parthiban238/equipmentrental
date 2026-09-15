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

    // ==========================================
    // SEND WELCOME EMAIL AFTER REGISTRATION
    // ==========================================
    public void sendWelcomeEmail(
            String toEmail,
            String name,
            String role) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Welcome to Agricultural Equipment Rental System"
        );

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


    // ==========================================
    // SEND LOGIN EMAIL AFTER SUCCESSFUL LOGIN
    // ==========================================
    public void sendLoginEmail(
            String toEmail,
            String name,
            String role) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Login Successful - Agricultural Equipment Rental System"
        );

        String emailBody =
                "Hello " + name + ",\n\n" +
                "You have successfully logged in to the " +
                "Agricultural Equipment Rental System.\n\n" +

                "Login Details:\n" +
                "Name: " + name + "\n" +
                "Role: " + role + "\n" +
                "Email: " + toEmail + "\n\n" +

                "If this login was not made by you, " +
                "please change your password immediately.\n\n" +

                "Thank you for using our platform!\n\n" +
                "Regards,\n" +
                "Agricultural Equipment Rental Team";

        message.setText(emailBody);

        mailSender.send(message);
    }
}