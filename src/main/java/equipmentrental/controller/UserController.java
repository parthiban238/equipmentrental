package equipmentrental.controller;

import equipmentrental.entity.User;
import equipmentrental.repository.UserRepository;
import equipmentrental.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private final Random random = new Random();

    // Login OTP storage
    private final ConcurrentHashMap<String, String> loginOtpStore =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> loginOtpExpiryStore =
            new ConcurrentHashMap<>();

    // Forgot password OTP storage
    private final ConcurrentHashMap<String, String> forgotOtpStore =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> forgotOtpExpiryStore =
            new ConcurrentHashMap<>();


    // =========================
    // REGISTER USER
    // =========================

    @PostMapping
    public ResponseEntity<?> registerUser(
            @RequestBody User user) {

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email is required"
                    ));
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email already registered"
                    ));
        }

        User savedUser = userRepository.save(user);

        try {

            emailService.sendWelcomeEmail(
                    savedUser.getEmail(),
                    savedUser.getName()
            );

        } catch (Exception e) {

            System.out.println(
                    "Welcome email could not be sent: "
                            + e.getMessage()
            );
        }

        return ResponseEntity.ok(savedUser);
    }


    // =========================
    // LOGIN - SEND OTP
    // =========================

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email and password are required"
                    ));
        }

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Invalid email or password"
                    ));
        }

        if (!user.getPassword().equals(password)) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Invalid email or password"
                    ));
        }

        // Generate OTP
        String otp = generateOtp();

        loginOtpStore.put(email, otp);

        loginOtpExpiryStore.put(
                email,
                System.currentTimeMillis()
                        + (5 * 60 * 1000)
        );

        try {

            emailService.sendOtpEmail(
                    user.getEmail(),
                    user.getName(),
                    otp
            );

        } catch (Exception e) {

            System.out.println(
                    "Login OTP email could not be sent: "
                            + e.getMessage()
            );

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message",
                            "Unable to send OTP email"
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP sent successfully",
                        "email",
                        email,
                        "requiresOtp",
                        true
                )
        );
    }


    // =========================
    // RESEND LOGIN OTP
    // =========================

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendLoginOtp(
            @RequestBody Map<String, String> data) {

        String email = data.get("email");

        if (email == null ||
                email.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email is required"
                    ));
        }

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        String newOtp = generateOtp();

        loginOtpStore.put(
                email,
                newOtp
        );

        loginOtpExpiryStore.put(
                email,
                System.currentTimeMillis()
                        + (5 * 60 * 1000)
        );

        try {

            emailService.sendOtpEmail(
                    user.getEmail(),
                    user.getName(),
                    newOtp
            );

        } catch (Exception e) {

            System.out.println(
                    "Resend OTP email could not be sent: "
                            + e.getMessage()
            );

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message",
                            "Unable to resend OTP email"
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "New OTP sent successfully",
                        "email",
                        email,
                        "requiresOtp",
                        true
                )
        );
    }


    // =========================
    // VERIFY LOGIN OTP
    // =========================

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyLoginOtp(
            @RequestBody Map<String, String> otpData) {

        String email = otpData.get("email");
        String otp = otpData.get("otp");

        if (email == null || otp == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email and OTP are required"
                    ));
        }

        String storedOtp =
                loginOtpStore.get(email);

        Long expiry =
                loginOtpExpiryStore.get(email);

        if (storedOtp == null ||
                expiry == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "OTP not found or expired"
                    ));
        }

        if (System.currentTimeMillis() > expiry) {

            loginOtpStore.remove(email);
            loginOtpExpiryStore.remove(email);

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "OTP expired"
                    ));
        }

        if (!storedOtp.equals(otp)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Invalid OTP"
                    ));
        }

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        loginOtpStore.remove(email);
        loginOtpExpiryStore.remove(email);

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "message",
                "Login successful"
        );

        response.put(
                "id",
                user.getId()
        );

        response.put(
                "name",
                user.getName()
        );

        response.put(
                "email",
                user.getEmail()
        );

        response.put(
                "role",
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }


    // =========================
    // FORGOT PASSWORD
    // SEND OTP
    // =========================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> data) {

        String email = data.get("email");

        if (email == null ||
                email.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email is required"
                    ));
        }

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Email not registered"
                    ));
        }

        String otp = generateOtp();

        forgotOtpStore.put(
                email,
                otp
        );

        forgotOtpExpiryStore.put(
                email,
                System.currentTimeMillis()
                        + (5 * 60 * 1000)
        );

        try {

            emailService.sendForgotPasswordOtpEmail(
                    user.getEmail(),
                    user.getName(),
                    otp
            );

        } catch (Exception e) {

            System.out.println(
                    "Forgot password OTP email could not be sent: "
                            + e.getMessage()
            );

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message",
                            "Unable to send OTP email"
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset OTP sent successfully",
                        "email",
                        email
                )
        );
    }


    // =========================
    // VERIFY FORGOT PASSWORD OTP
    // =========================

    @PostMapping("/verify-forgot-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(
            @RequestBody Map<String, String> data) {

        String email = data.get("email");
        String otp = data.get("otp");

        if (email == null || otp == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email and OTP are required"
                    ));
        }

        String storedOtp =
                forgotOtpStore.get(email);

        Long expiry =
                forgotOtpExpiryStore.get(email);

        if (storedOtp == null ||
                expiry == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "OTP not found or expired"
                    ));
        }

        if (System.currentTimeMillis() > expiry) {

            forgotOtpStore.remove(email);
            forgotOtpExpiryStore.remove(email);

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "OTP expired"
                    ));
        }

        if (!storedOtp.equals(otp)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Invalid OTP"
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP verified successfully",
                        "verified",
                        true
                )
        );
    }


    // =========================
    // RESET PASSWORD
    // =========================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> data) {

        String email = data.get("email");
        String otp = data.get("otp");
        String newPassword =
                data.get("newPassword");

        if (email == null ||
                otp == null ||
                newPassword == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email, OTP and new password are required"
                    ));
        }

        if (newPassword.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "New password cannot be empty"
                    ));
        }

        String storedOtp =
                forgotOtpStore.get(email);

        Long expiry =
                forgotOtpExpiryStore.get(email);

        if (storedOtp == null ||
                expiry == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "OTP not found or expired"
                    ));
        }

        if (System.currentTimeMillis() > expiry) {

            forgotOtpStore.remove(email);
            forgotOtpExpiryStore.remove(email);

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "OTP expired"
                    ));
        }

        if (!storedOtp.equals(otp)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Invalid OTP"
                    ));
        }

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        user.setPassword(newPassword);

        userRepository.save(user);

        forgotOtpStore.remove(email);
        forgotOtpExpiryStore.remove(email);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully"
                )
        );
    }


    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id) {

        User user =
                userRepository.findById(id)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        return ResponseEntity.ok(user);
    }


    // =========================
    // GET USER BY EMAIL
    // =========================

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(
            @PathVariable String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        return ResponseEntity.ok(user);
    }


    // =========================
    // GET USERS BY ROLE
    // =========================

    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(
            @PathVariable String role) {

        return userRepository.findByRole(role);
    }


    // =========================
    // UPDATE USER
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        User existingUser =
                userRepository.findById(id)
                        .orElse(null);

        if (existingUser == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        existingUser.setName(
                updatedUser.getName()
        );

        existingUser.setEmail(
                updatedUser.getEmail()
        );

        existingUser.setPassword(
                updatedUser.getPassword()
        );

        existingUser.setRole(
                updatedUser.getRole()
        );

        User savedUser =
                userRepository.save(existingUser);

        return ResponseEntity.ok(savedUser);
    }


    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id) {

        if (!userRepository.existsById(id)) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "User not found"
                    ));
        }

        userRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "User deleted successfully"
                )
        );
    }


    // =========================
    // GENERATE 6 DIGIT OTP
    // =========================

    private String generateOtp() {

        return String.format(
                "%06d",
                random.nextInt(1000000)
        );
    }
}