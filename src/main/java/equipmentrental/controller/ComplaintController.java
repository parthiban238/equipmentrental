package equipmentrental.controller;

import equipmentrental.entity.Complaint;
import equipmentrental.entity.Equipment;
import equipmentrental.repository.ComplaintRepository;
import equipmentrental.repository.EquipmentRepository;
import equipmentrental.repository.UserRepository;
import equipmentrental.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "https://equipmentrental-3.onrender.com"
})
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ComplaintController(
            ComplaintRepository complaintRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.complaintRepository = complaintRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // CREATE COMPLAINT
    @PostMapping
    public ResponseEntity<?> createComplaint(
            @RequestBody Complaint complaint) {

        if (complaint.getRentalId() == null) {
            return ResponseEntity.badRequest()
                    .body("Rental ID is required");
        }

        if (complaint.getUserId() == null) {
            return ResponseEntity.badRequest()
                    .body("User ID is required");
        }

        if (complaint.getEquipmentId() == null) {
            return ResponseEntity.badRequest()
                    .body("Equipment ID is required");
        }

        if (complaint.getComplaintType() == null ||
                complaint.getComplaintType().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Complaint type is required");
        }

        if (complaint.getDescription() == null ||
                complaint.getDescription().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Complaint description is required");
        }

        Equipment equipment = equipmentRepository
                .findById(complaint.getEquipmentId())
                .orElse(null);

        if (equipment == null) {
            return ResponseEntity.badRequest()
                    .body("Equipment not found");
        }

        Long ownerId = equipment.getOwnerId();

        if (ownerId == null) {
            return ResponseEntity.badRequest()
                    .body("Owner not assigned to this equipment");
        }

        complaint.setStatus("PENDING");
        complaint.setCreatedAt(LocalDateTime.now());

        Complaint savedComplaint =
                complaintRepository.save(complaint);

        userRepository.findById(ownerId)
                .ifPresentOrElse(
                        owner -> {

                            try {

                                emailService.sendComplaintEmail(
                                        owner.getEmail(),
                                        owner.getName(),
                                        savedComplaint.getId(),
                                        savedComplaint.getRentalId(),
                                        savedComplaint.getEquipmentId(),
                                        savedComplaint.getComplaintType(),
                                        savedComplaint.getDescription()
                                );

                                System.out.println(
                                        "Complaint email sent successfully"
                                );

                                System.out.println(
                                        "Owner ID: " + ownerId
                                );

                                System.out.println(
                                        "Owner Email: " +
                                        owner.getEmail()
                                );

                                System.out.println(
                                        "Complaint ID: " +
                                        savedComplaint.getId()
                                );

                            } catch (Exception e) {

                                System.out.println(
                                        "Complaint email failed"
                                );

                                System.out.println(
                                        "Owner Email: " +
                                        owner.getEmail()
                                );

                                System.out.println(
                                        "Error: " +
                                        e.getMessage()
                                );
                            }
                        },

                        () -> {

                            System.out.println(
                                    "OWNER NOT FOUND: " +
                                    ownerId
                            );
                        }
                );

        return ResponseEntity.ok(savedComplaint);
    }

    // GET ALL COMPLAINTS
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {

        List<Complaint> complaints =
                complaintRepository.findAll();

        return ResponseEntity.ok(complaints);
    }

    // GET COMPLAINT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable Long id) {

        return complaintRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    // GET COMPLAINTS BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> getComplaintsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                complaintRepository.findByUserId(userId)
        );
    }

    // GET COMPLAINTS BY EQUIPMENT
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<Complaint>> getComplaintsByEquipment(
            @PathVariable Long equipmentId) {

        return ResponseEntity.ok(
                complaintRepository.findByEquipmentId(equipmentId)
        );
    }

    // GET COMPLAINTS BY RENTAL
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Complaint>> getComplaintsByRental(
            @PathVariable Long rentalId) {

        return ResponseEntity.ok(
                complaintRepository.findByRentalId(rentalId)
        );
    }

    // GET COMPLAINTS BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(
            @PathVariable String status) {

        String normalizedStatus =
                status.trim().toUpperCase();

        return ResponseEntity.ok(
                complaintRepository.findByStatus(
                        normalizedStatus
                )
        );
    }

    // GET COMPLAINTS BY OWNER
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getComplaintsByOwner(
            @PathVariable Long ownerId) {

        List<Complaint> allComplaints =
                complaintRepository.findAll();

        List<Complaint> ownerComplaints =
                allComplaints.stream()
                        .filter(complaint -> {

                            Equipment equipment =
                                    equipmentRepository
                                            .findById(
                                                    complaint.getEquipmentId()
                                            )
                                            .orElse(null);

                            if (equipment == null) {
                                return false;
                            }

                            return equipment.getOwnerId() != null
                                    && equipment.getOwnerId()
                                    .equals(ownerId);
                        })
                        .toList();

        return ResponseEntity.ok(ownerComplaints);
    }

    // UPDATE COMPLAINT STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Complaint request) {

        Complaint complaint =
                complaintRepository
                        .findById(id)
                        .orElse(null);

        if (complaint == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getStatus() == null ||
                request.getStatus().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Status is required");
        }

        String newStatus =
                request.getStatus()
                        .trim()
                        .toUpperCase();

        if (!newStatus.equals("PENDING") &&
                !newStatus.equals("UNDER_REVIEW") &&
                !newStatus.equals("RESOLVED") &&
                !newStatus.equals("REJECTED")) {

            return ResponseEntity.badRequest()
                    .body(
                            "Invalid status. Allowed values: " +
                            "PENDING, UNDER_REVIEW, " +
                            "RESOLVED, REJECTED"
                    );
        }

        complaint.setStatus(newStatus);

        Complaint updatedComplaint =
                complaintRepository.save(complaint);

        return ResponseEntity.ok(updatedComplaint);
    }

    // DELETE COMPLAINT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(
            @PathVariable Long id) {

        Complaint complaint =
                complaintRepository
                        .findById(id)
                        .orElse(null);

        if (complaint == null) {
            return ResponseEntity.notFound().build();
        }

        complaintRepository.delete(complaint);

        return ResponseEntity.ok(
                "Complaint deleted successfully"
        );
    }
}