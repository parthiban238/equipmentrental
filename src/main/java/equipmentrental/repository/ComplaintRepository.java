package equipmentrental.repository;

import equipmentrental.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserId(Long userId);

    List<Complaint> findByEquipmentId(Long equipmentId);

    List<Complaint> findByStatus(String status);

    List<Complaint> findByRentalId(Long rentalId);
}