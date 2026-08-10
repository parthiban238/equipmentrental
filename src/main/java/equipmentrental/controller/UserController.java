package equipmentrental.controller;

import equipmentrental.entity.User;
import equipmentrental.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // Create/Register user
    @PostMapping
    public User registerUser(@RequestBody User user) {
        return repository.save(user);
    }

    // Login user
    @PostMapping("/login")
    public User login(@RequestBody User loginUser) {

        return repository.findByEmail(loginUser.getEmail())
                .filter(user ->
                        user.getPassword().equals(loginUser.getPassword()))
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));
    }

    // Get user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // Get users by role
    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(
            @PathVariable String role) {

        return repository.findByRole(role);
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public User getUserByEmail(
            @PathVariable String email) {

        return repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: " + email));
    }

    // Update user
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        user.setRole(updatedUser.getRole());

        return repository.save(user);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        repository.delete(user);

        return "User deleted successfully";
    }
}