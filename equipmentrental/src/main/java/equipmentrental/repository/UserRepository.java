
package equipmentrental.repository;

import equipmentrental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find users by role
    List<User> findByRole(String role);

    // Find user by email
    Optional<User> findByEmail(String email);
}

