
package equipmentrental.repository;

import equipmentrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository
        extends JpaRepository<Rental, Long> {

    // Get rentals by user
    List<Rental> findByUserId(Long userId);

    // Get rentals by status
    List<Rental> findByStatus(String status);

    // Get rentals by equipment and active status
    List<Rental> findByEquipmentIdAndStatusIn(
            Long equipmentId,
            List<String> statuses);

    // Get rentals for multiple equipment
    List<Rental> findByEquipmentIdIn(
            List<Long> equipmentIds);
}

