package equipmentrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String name) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(
                "Welcome to Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + name + ",\n\n" +
                "Welcome to the Agricultural Equipment Rental System!\n\n" +
                "Your account has been successfully created.\n\n" +
                "You can now login and rent agricultural equipment " +
                "or manage your equipment based on your role.\n\n" +
                "Thank you for joining us!\n\n" +
                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }

    public void sendOtpEmail(
            String toEmail,
            String name,
            String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(
                "Login OTP - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + name + ",\n\n" +
                "Your OTP for login is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "Please do not share this OTP with anyone.\n\n" +
                "If you did not attempt to login, please ignore this email.\n\n" +
                "Thank you,\n" +
                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }

    public void sendForgotPasswordOtpEmail(
            String toEmail,
            String name,
            String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(
                "Password Reset OTP - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + name + ",\n\n" +
                "We received a request to reset your password.\n\n" +
                "Your password reset OTP is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "Enter this OTP on the password reset page " +
                "to create a new password.\n\n" +
                "Please do not share this OTP with anyone.\n\n" +
                "If you did not request a password reset, " +
                "please ignore this email.\n\n" +
                "Thank you,\n" +
                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }

    public void sendComplaintEmail(
            String toEmail,
            String ownerName,
            Long complaintId,
            Long rentalId,
            Long equipmentId,
            String complaintType,
            String description) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "New Customer Complaint - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + ownerName + ",\n\n" +
                "A new complaint has been reported by a farmer " +
                "regarding your agricultural equipment.\n\n" +

                "Complaint ID: " + complaintId + "\n" +
                "Rental ID: " + rentalId + "\n" +
                "Equipment ID: " + equipmentId + "\n" +
                "Complaint Type: " + complaintType + "\n\n" +

                "Description:\n" +
                description + "\n\n" +

                "Complaint Status: PENDING\n\n" +

                "Please login to the Agricultural Equipment Rental System " +
                "and review the complaint.\n\n" +

                "Thank you,\n" +
                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }
}