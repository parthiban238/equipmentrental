package equipmentrental.controller;

import equipmentrental.entity.Equipment;
import equipmentrental.entity.Rental;
import equipmentrental.repository.EquipmentRepository;
import equipmentrental.repository.RentalRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
})
public class RentalController {

    private final RentalRepository repository;
    private final EquipmentRepository equipmentRepository;

    public RentalController(
            RentalRepository repository,
            EquipmentRepository equipmentRepository) {

        this.repository = repository;
        this.equipmentRepository = equipmentRepository;
    }

    // Get all rentals
    @GetMapping
    public List<Rental> getAllRentals() {
        return repository.findAll();
    }

    // Create rental
    @PostMapping
    public Rental createRental(@RequestBody Rental rental) {

        Equipment equipment = equipmentRepository
                .findById(rental.getEquipmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: "
                                        + rental.getEquipmentId()));

        // Check dates
        if (rental.getStartDate() == null ||
                rental.getEndDate() == null) {

            throw new RuntimeException(
                    "Start date and end date are required");
        }

        if (rental.getEndDate()
                .isBefore(rental.getStartDate())) {

            throw new RuntimeException(
                    "End date cannot be before start date");
        }

        // Check equipment availability
        if (!equipment.isAvailable()) {
            throw new RuntimeException(
                    "Equipment is currently unavailable");
        }

        // Check overlapping bookings
        List<String> activeStatuses =
                List.of("PENDING", "APPROVED");

        List<Rental> existingRentals =
                repository.findByEquipmentIdAndStatusIn(
                        rental.getEquipmentId(),
                        activeStatuses);

        for (Rental existing : existingRentals) {

            boolean overlap =
                    !rental.getStartDate()
                            .isAfter(existing.getEndDate())
                    &&
                    !rental.getEndDate()
                            .isBefore(existing.getStartDate());

            if (overlap) {
                throw new RuntimeException(
                        "Equipment is already booked for these dates");
            }
        }

        // Calculate rental days
        long rentalDays =
                ChronoUnit.DAYS.between(
                        rental.getStartDate(),
                        rental.getEndDate()) + 1;

        // Calculate total amount
        double totalAmount =
                rentalDays * equipment.getPricePerDay();

        rental.setTotalAmount(totalAmount);

        // Default status
        rental.setStatus("PENDING");

        return repository.save(rental);
    }

    // Get rental by ID
    @GetMapping("/id/{id}")
    public Rental getRentalById(
            @PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));
    }

    // Get rentals by user
    @GetMapping("/user/{userId}")
    public List<Rental> getRentalsByUser(
            @PathVariable Long userId) {

        return repository.findByUserId(userId);
    }

    // Get rentals by owner
    @GetMapping("/owner/{ownerId}")
    public List<Rental> getRentalsByOwner(
            @PathVariable Long ownerId) {

        // Find owner's equipment
        List<Equipment> ownerEquipment =
                equipmentRepository.findByOwnerId(ownerId);

        // Get equipment IDs
        List<Long> equipmentIds =
                ownerEquipment.stream()
                        .map(Equipment::getId)
                        .collect(Collectors.toList());

        // Owner has no equipment
        if (equipmentIds.isEmpty()) {
            return List.of();
        }

        // Get rentals for owner's equipment
        return repository.findByEquipmentIdIn(equipmentIds);
    }

    // Get pending rentals
    @GetMapping("/pending")
    public List<Rental> getPendingRentals() {
        return repository.findByStatus("PENDING");
    }

    // Get rentals by status
    @GetMapping("/status/{status}")
    public List<Rental> getRentalsByStatus(
            @PathVariable String status) {

        return repository.findByStatus(status);
    }

    // Check equipment availability
    @GetMapping("/availability")
    public String checkAvailability(
            @RequestParam Long equipmentId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDate start =
                LocalDate.parse(startDate);

        LocalDate end =
                LocalDate.parse(endDate);

        if (end.isBefore(start)) {
            return "End date cannot be before start date";
        }

        List<String> activeStatuses =
                List.of("PENDING", "APPROVED");

        List<Rental> existingRentals =
                repository.findByEquipmentIdAndStatusIn(
                        equipmentId,
                        activeStatuses);

        for (Rental existing : existingRentals) {

            boolean overlap =
                    !start.isAfter(existing.getEndDate())
                    &&
                    !end.isBefore(existing.getStartDate());

            if (overlap) {
                return "Equipment is NOT available for these dates";
            }
        }

        return "Equipment is AVAILABLE for these dates";
    }

    // Update rental
    @PutMapping("/id/{id}")
    public Rental updateRental(
            @PathVariable Long id,
            @RequestBody Rental updatedRental) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        rental.setUserId(updatedRental.getUserId());
        rental.setEquipmentId(updatedRental.getEquipmentId());
        rental.setStartDate(updatedRental.getStartDate());
        rental.setEndDate(updatedRental.getEndDate());
        rental.setTotalAmount(updatedRental.getTotalAmount());
        rental.setStatus(updatedRental.getStatus());

        return repository.save(rental);
    }

    // Approve rental
    @PutMapping("/approve/{id}")
    public Rental approveRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        Equipment equipment = equipmentRepository
                .findById(rental.getEquipmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: "
                                        + rental.getEquipmentId()));

        if (!equipment.isAvailable()) {
            throw new RuntimeException(
                    "Equipment is already unavailable");
        }

        rental.setStatus("APPROVED");

        equipment.setAvailable(false);

        equipmentRepository.save(equipment);

        return repository.save(rental);
    }

    // Reject rental
    @PutMapping("/reject/{id}")
    public Rental rejectRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        rental.setStatus("REJECTED");

        return repository.save(rental);
    }

    // Complete rental
    @PutMapping("/complete/{id}")
    public Rental completeRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        Equipment equipment = equipmentRepository
                .findById(rental.getEquipmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: "
                                        + rental.getEquipmentId()));

        rental.setStatus("COMPLETED");

        // Make equipment available again
        equipment.setAvailable(true);

        equipmentRepository.save(equipment);

        return repository.save(rental);
    }

    // Delete rental
    @DeleteMapping("/id/{id}")
    public String deleteRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        repository.delete(rental);

        return "Rental deleted successfully";
    }
}