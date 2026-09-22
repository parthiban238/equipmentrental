package equipmentrental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

@Service
public class EmailService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

    private static final URI BREVO_EMAIL_URI =
            URI.create("https://api.brevo.com/v3/smtp/email");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private final String brevoApiKey;
    private final String senderEmail;
    private final String senderName;

    public EmailService(
            @Value("${brevo.api.key}") String brevoApiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name}") String senderName) {
        this.brevoApiKey = brevoApiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

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
    }

    public void sendOtpEmail(String toEmail, String name, String otp) {
        sendEmail(
                toEmail,
                "Login OTP - Agricultural Equipment Rental System",
                "Hello " + name + ",\n\n" +
                        "Your OTP for login is:\n\n" +
                        otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "Please do not share this OTP with anyone.\n\n" +
                        "If you did not attempt to login, please ignore this email.\n\n" +
                        "Thank you,\n" +
                        "Agricultural Equipment Rental System");
    }

    public void sendForgotPasswordOtpEmail(
            String toEmail,
            String name,
            String otp) {
        sendEmail(
                toEmail,
                "Password Reset OTP - Agricultural Equipment Rental System",
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
                        "Agricultural Equipment Rental System");
    }

    public void sendBookingCreatedEmail(
            String toEmail,
            String farmerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {
        sendEmail(
                toEmail,
                "Booking Successful - Agricultural Equipment Rental System",
                bookingDetails(
                        "Hello " + farmerName + ",\n\n" +
                                "Your agricultural equipment booking has been successfully created.\n\n",
                        rentalId,
                        equipmentName,
                        startDate,
                        endDate,
                        totalAmount) +
                        "Booking Status: PENDING\n\n" +
                        "Your booking request has been sent to the equipment owner for approval.\n\n" +
                        "You will receive another email when the owner approves or rejects your booking.\n\n" +
                        "Thank you for using the Agricultural Equipment Rental System.\n\n" +
                        "Regards,\nAgricultural Equipment Rental System");
    }

    public void sendBookingRequestToOwnerEmail(
            String toEmail,
            String ownerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {
        sendEmail(
                toEmail,
                "New Booking Request - Agricultural Equipment Rental System",
                bookingDetails(
                        "Hello " + ownerName + ",\n\n" +
                                "You have received a new booking request for your agricultural equipment.\n\n",
                        rentalId,
                        equipmentName,
                        startDate,
                        endDate,
                        totalAmount) +
                        "Booking Status: PENDING\n\n" +
                        "Please login to the Agricultural Equipment Rental System and review this booking request.\n\n" +
                        "You can approve or reject the booking from your Owner Dashboard.\n\n" +
                        "Thank you,\nAgricultural Equipment Rental System");
    }

    public void sendComplaintEmail(
            String toEmail,
            String ownerName,
            Long complaintId,
            Long rentalId,
            Long equipmentId,
            String complaintType,
            String description) {
        sendEmail(
                toEmail,
                "New Customer Complaint - Agricultural Equipment Rental System",
                "Hello " + ownerName + ",\n\n" +
                        "A new complaint has been reported by a farmer regarding your agricultural equipment.\n\n" +
                        "Complaint ID: " + complaintId + "\n" +
                        "Rental ID: " + rentalId + "\n" +
                        "Equipment ID: " + equipmentId + "\n" +
                        "Complaint Type: " + complaintType + "\n\n" +
                        "Description:\n" + description + "\n\n" +
                        "Complaint Status: PENDING\n\n" +
                        "Please login to the Agricultural Equipment Rental System and review the complaint.\n\n" +
                        "Thank you,\nAgricultural Equipment Rental System");
    }

    public void sendBookingApprovedEmail(
            String toEmail,
            String farmerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {
        sendEmail(
                toEmail,
                "Booking Approved - Agricultural Equipment Rental System",
                bookingDetails(
                        "Hello " + farmerName + ",\n\n" +
                                "Good news! Your equipment booking has been APPROVED by the equipment owner.\n\n",
                        rentalId,
                        equipmentName,
                        startDate,
                        endDate,
                        totalAmount) +
                        "Booking Status: APPROVED\n\n" +
                        "You can login to the Agricultural Equipment Rental System to view your booking details.\n\n" +
                        "Thank you for using our service.\n\n" +
                        "Agricultural Equipment Rental System");
    }

    public void sendBookingRejectedEmail(
            String toEmail,
            String farmerName,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {
        sendEmail(
                toEmail,
                "Booking Rejected - Agricultural Equipment Rental System",
                bookingDetails(
                        "Hello " + farmerName + ",\n\n" +
                                "Your equipment booking request has been REJECTED by the equipment owner.\n\n",
                        rentalId,
                        equipmentName,
                        startDate,
                        endDate,
                        totalAmount) +
                        "Booking Status: REJECTED\n\n" +
                        "You can login to the Agricultural Equipment Rental System to view your booking details and make another booking.\n\n" +
                        "Thank you,\n\nAgricultural Equipment Rental System");
    }

    private String bookingDetails(
            String introduction,
            Long rentalId,
            String equipmentName,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount) {
        return introduction +
                "Booking Details:\n\n" +
                "Booking ID: " + rentalId + "\n" +
                "Equipment: " + equipmentName + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n\n";
    }

    private void sendEmail(String toEmail, String subject, String text) {
        String requestBody = "{" +
                "\"sender\":{" +
                "\"name\":\"" + escapeJson(senderName) + "\"," +
                "\"email\":\"" + escapeJson(senderEmail) + "\"}," +
                "\"to\":[{\"email\":\"" + escapeJson(toEmail) + "\"}]," +
                "\"subject\":\"" + escapeJson(subject) + "\"," +
                "\"textContent\":\"" + escapeJson(text) + "\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder(BREVO_EMAIL_URI)
                .timeout(Duration.ofSeconds(30))
                .header("accept", "application/json")
                .header("api-key", brevoApiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                logger.error(
                        "Brevo email request failed with HTTP status {}: {}",
                        response.statusCode(),
                        response.body());
                throw new IllegalStateException(
                        "Brevo email request failed with HTTP status " +
                                response.statusCode());
            }
        } catch (IOException e) {
            logger.error("Brevo email request failed: {}", e.getMessage());
            throw new IllegalStateException("Brevo email request failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Brevo email request was interrupted: {}", e.getMessage());
            throw new IllegalStateException("Brevo email request was interrupted", e);
        }
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 16);

        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }
}
