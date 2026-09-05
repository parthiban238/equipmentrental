package equipmentrental.repository;

import equipmentrental.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository
        extends JpaRepository<Rating, Long> {

    Optional<Rating> findByRentalId(Long rentalId);

    List<Rating> findByUserId(Long userId);

    List<Rating> findByEquipmentId(Long equipmentId);

    boolean existsByRentalId(Long rentalId);
}