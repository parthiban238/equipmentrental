
package equipmentrental.repository;

import equipmentrental.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    // Find only available equipment
    List<Equipment> findByAvailableTrue();

    // Find equipment by category
    List<Equipment> findByCategory(String category);

    // Find equipment by location
    List<Equipment> findByLocation(String location);

    // Find equipment by owner
    List<Equipment> findByOwnerId(Long ownerId);
}

