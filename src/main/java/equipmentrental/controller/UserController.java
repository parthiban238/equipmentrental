package equipmentrental.controller;

import equipmentrental.entity.User;
import equipmentrental.repository.UserRepository;
import equipmentrental.service.EmailService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;
    private final EmailService emailService;

    // Constructor
    public UserController(
            UserRepository repository,
            EmailService emailService) {

        this.repository = repository;
        this.emailService = emailService;
    }

    // ==========================================
    // GET ALL USERS
    // ==========================================
    @GetMapping
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // ==========================================
    // REGISTER USER + WELCOME EMAIL
    // ==========================================
    @PostMapping
    public User registerUser(@RequestBody User user) {

        // Check duplicate email
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Save user
        User savedUser = repository.save(user);

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(
                    savedUser.getEmail(),
                    savedUser.getName(),
                    savedUser.getRole()
            );

            System.out.println(
                    "Welcome email sent successfully to: "
                            + savedUser.getEmail()
            );

        } catch (Exception e) {
            System.out.println(
                    "Welcome email could not be sent: "
                            + e.getMessage()
            );
        }

        return savedUser;
    }

    // ==========================================
    // LOGIN USER + LOGIN EMAIL
    // ==========================================
    @PostMapping("/login")
    public User login(@RequestBody User loginUser) {

        // Check email and password
        User user = repository.findByEmail(loginUser.getEmail())
                .filter(existingUser ->
                        existingUser.getPassword()
                                .equals(loginUser.getPassword()))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        ));

        // Send login notification email
        try {
            emailService.sendLoginEmail(
                    user.getEmail(),
                    user.getName(),
                    user.getRole()
            );

            System.out.println(
                    "Login email sent successfully to: "
                            + user.getEmail()
            );

        } catch (Exception e) {
            System.out.println(
                    "Login email could not be sent: "
                            + e.getMessage()
            );
        }

        return user;
    }

    // ==========================================
    // GET USER BY ID
    // ==========================================
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    // ==========================================
    // GET USERS BY ROLE
    // ==========================================
    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(
            @PathVariable String role) {

        return repository.findByRole(role);
    }

    // ==========================================
    // GET USER BY EMAIL
    // ==========================================
    @GetMapping("/email/{email}")
    public User getUserByEmail(
            @PathVariable String email) {

        return repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: "
                                        + email
                        ));
    }

    // ==========================================
    // UPDATE USER
    // ==========================================
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        user.setRole(updatedUser.getRole());

        return repository.save(user);
    }

    // ==========================================
    // DELETE USER
    // ==========================================
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        repository.delete(user);

        return "User deleted successfully";
    }
}