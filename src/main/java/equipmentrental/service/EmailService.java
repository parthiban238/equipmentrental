package equipmentrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    // ==========================================
    // WELCOME EMAIL
    // ==========================================

    public void sendWelcomeEmail(
            String toEmail,
            String name) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Welcome to Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + name + ",\n\n" +
            public void sendWelcomeEmail(String toEmail, String name) {
                sendEmail(
                        toEmail,
                        "Welcome to Agricultural Equipment Rental System",
                        "Hello " + name + ",\n\n" +
                                "Welcome to the Agricultural Equipment Rental System!\n\n" +
                                "Your account has been successfully created.\n\n" +
                                "You can now login and rent agricultural equipment " +
                                "or manage your equipment based on your role.\n\n" +
                                "Thank you for joining us!\n\n" +
                                "Agricultural Equipment Rental System");
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


    // ==========================================
    // FORGOT PASSWORD OTP
    // ==========================================

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


    // ==========================================
    // BOOKING CREATED / BOOKING SUCCESS EMAIL
    // TO FARMER
    // ==========================================

    public void sendBookingCreatedEmail(
            String toEmail,
            String farmerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Booking Successful - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + farmerName + ",\n\n" +

                "Your agricultural equipment booking has been " +
                "successfully created.\n\n" +

                "Booking Details:\n\n" +

                "Booking ID: " + rentalId + "\n" +
                "Equipment: " + equipmentName + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n\n" +

                "Booking Status: PENDING\n\n" +

                "Your booking request has been sent to the equipment owner " +
                "for approval.\n\n" +

                "You will receive another email when the owner approves " +
                "or rejects your booking.\n\n" +

                "Thank you for using the Agricultural Equipment Rental System.\n\n" +

                "Regards,\n" +
                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }


    // ==========================================
    // NEW BOOKING REQUEST EMAIL
    // TO OWNER
    // ==========================================

    public void sendBookingRequestToOwnerEmail(
            String toEmail,
            String ownerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "New Booking Request - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + ownerName + ",\n\n" +

                "You have received a new booking request " +
                "for your agricultural equipment.\n\n" +

                "Booking Details:\n\n" +

                "Booking ID: " + rentalId + "\n" +
                "Equipment: " + equipmentName + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n\n" +

                "Booking Status: PENDING\n\n" +

                "Please login to the Agricultural Equipment Rental System " +
                "and review this booking request.\n\n" +

                "You can approve or reject the booking " +
                "from your Owner Dashboard.\n\n" +

                "Thank you,\n" +
                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }


    // ==========================================
    // COMPLAINT EMAIL TO OWNER
    // ==========================================

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


    // ==========================================
    // BOOKING APPROVED EMAIL TO FARMER
    // ==========================================

    public void sendBookingApprovedEmail(
            String toEmail,
            String farmerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Booking Approved - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + farmerName + ",\n\n" +

                "Good news! Your equipment booking has been APPROVED " +
                "by the equipment owner.\n\n" +

                "Booking Details:\n\n" +

                "Booking ID: " + rentalId + "\n" +
                "Equipment: " + equipmentName + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n\n" +

                "Booking Status: APPROVED\n\n" +

                "You can login to the Agricultural Equipment Rental System " +
                "to view your booking details.\n\n" +

                "Thank you for using our service.\n\n" +

                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }


    // ==========================================
    // BOOKING REJECTED EMAIL TO FARMER
    // ==========================================

    public void sendBookingRejectedEmail(
            String toEmail,
            String farmerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Booking Rejected - Agricultural Equipment Rental System"
        );

        message.setText(
                "Hello " + farmerName + ",\n\n" +

                "Your equipment booking request has been REJECTED " +
                "by the equipment owner.\n\n" +

                "Booking Details:\n\n" +

                "Booking ID: " + rentalId + "\n" +
                "Equipment: " + equipmentName + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n\n" +

                "Booking Status: REJECTED\n\n" +

                "You can login to the Agricultural Equipment Rental System " +
                "to view your booking details and make another booking.\n\n" +

                "Thank you,\n\n" +

                "Agricultural Equipment Rental System"
        );

        mailSender.send(message);
    }
}