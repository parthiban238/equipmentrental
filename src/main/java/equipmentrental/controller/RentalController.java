package equipmentrental.controller;

import equipmentrental.entity.Equipment;
import equipmentrental.entity.Rental;
import equipmentrental.repository.EquipmentRepository;
import equipmentrental.repository.RentalRepository;
import equipmentrental.repository.UserRepository;
import equipmentrental.service.EmailService;

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
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RentalController(
            RentalRepository repository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.repository = repository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    // ==========================================
    // GET ALL RENTALS
    // ==========================================

    @GetMapping
    public List<Rental> getAllRentals() {
        return repository.findAll();
    }


    // ==========================================
    // CREATE RENTAL
    // FARMER EMAIL + OWNER EMAIL
    // ==========================================

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

        LocalDate today = LocalDate.now();

        if (rental.getStartDate().isBefore(today)) {

            throw new RuntimeException(
                    "Booking cannot be made for a past date");
        }

        if (rental.getEndDate().isBefore(today)) {

            throw new RuntimeException(
                    "End date cannot be in the past");
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


        // Check existing bookings

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

        rental.setStatus("PENDING");


        // ==========================================
        // SAVE RENTAL
        // ==========================================

        Rental savedRental =
                repository.save(rental);


        // ==========================================
        // FARMER BOOKING CONFIRMATION EMAIL
        // ==========================================

        userRepository.findById(savedRental.getUserId())
                .ifPresentOrElse(

                        farmer -> {

                            try {

                                emailService.sendBookingCreatedEmail(
                                        farmer.getEmail(),
                                        farmer.getName(),
                                        savedRental.getId(),
                                        equipment.getName(),
                                        savedRental.getStartDate(),
                                        savedRental.getEndDate(),
                                        savedRental.getTotalAmount()
                                );

                                System.out.println(
                                        "Farmer booking email sent successfully");

                            } catch (Exception e) {

                                System.out.println(
                                        "Farmer booking email failed: "
                                                + e.getMessage());
                            }
                        },

                        () -> {

                            System.out.println(
                                    "FARMER NOT FOUND FOR USER ID: "
                                            + savedRental.getUserId());
                        }
                );


        // ==========================================
        // OWNER BOOKING REQUEST EMAIL
        // ==========================================

        if (equipment.getOwnerId() != null) {

            userRepository.findById(equipment.getOwnerId())
                    .ifPresentOrElse(

                            owner -> {

                                try {

                                    emailService.sendBookingRequestToOwnerEmail(
                                            owner.getEmail(),
                                            owner.getName(),
                                            savedRental.getId(),
                                            equipment.getName(),
                                            savedRental.getStartDate(),
                                            savedRental.getEndDate(),
                                            savedRental.getTotalAmount()
                                    );

                                    System.out.println(
                                            "Owner booking request email sent successfully");

                                } catch (Exception e) {

                                    System.out.println(
                                            "Owner booking email failed: "
                                                    + e.getMessage());
                                }
                            },

                            () -> {

                                System.out.println(
                                        "OWNER NOT FOUND FOR OWNER ID: "
                                                + equipment.getOwnerId());
                            }
                    );
        }


        return savedRental;
    }


    // ==========================================
    // GET RENTAL BY ID
    // ==========================================

    @GetMapping("/id/{id}")
    public Rental getRentalById(
            @PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));
    }


    // ==========================================
    // GET RENTALS BY USER
    // ==========================================

    @GetMapping("/user/{userId}")
    public List<Rental> getRentalsByUser(
            @PathVariable Long userId) {

        return repository.findByUserId(userId);
    }


    // ==========================================
    // GET RENTALS BY OWNER
    // ==========================================

    @GetMapping("/owner/{ownerId}")
    public List<Rental> getRentalsByOwner(
            @PathVariable Long ownerId) {

        List<Equipment> ownerEquipment =
                equipmentRepository.findByOwnerId(ownerId);

        List<Long> equipmentIds =
                ownerEquipment.stream()
                        .map(Equipment::getId)
                        .collect(Collectors.toList());

        if (equipmentIds.isEmpty()) {

            return List.of();
        }

        return repository.findByEquipmentIdIn(equipmentIds);
    }


    // ==========================================
    // GET PENDING RENTALS
    // ==========================================

    @GetMapping("/pending")
    public List<Rental> getPendingRentals() {

        return repository.findByStatus("PENDING");
    }


    // ==========================================
    // GET RENTALS BY STATUS
    // ==========================================

    @GetMapping("/status/{status}")
    public List<Rental> getRentalsByStatus(
            @PathVariable String status) {

        return repository.findByStatus(status);
    }


    // ==========================================
    // CHECK AVAILABILITY
    // ==========================================

    @GetMapping("/availability")
    public String checkAvailability(
            @RequestParam Long equipmentId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDate start =
                LocalDate.parse(startDate);

        LocalDate end =
                LocalDate.parse(endDate);

        LocalDate today =
                LocalDate.now();

        if (start.isBefore(today)) {

            return "Booking cannot be made for a past date";
        }

        if (end.isBefore(today)) {

            return "End date cannot be in the past";
        }

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


    // ==========================================
    // UPDATE RENTAL
    // ==========================================

    @PutMapping("/id/{id}")
    public Rental updateRental(
            @PathVariable Long id,
            @RequestBody Rental updatedRental) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        LocalDate today =
                LocalDate.now();

        if (updatedRental.getStartDate() == null ||
                updatedRental.getEndDate() == null) {

            throw new RuntimeException(
                    "Start date and end date are required");
        }

        if (updatedRental.getStartDate()
                .isBefore(today)) {

            throw new RuntimeException(
                    "Start date cannot be in the past");
        }

        if (updatedRental.getEndDate()
                .isBefore(today)) {

            throw new RuntimeException(
                    "End date cannot be in the past");
        }

        if (updatedRental.getEndDate()
                .isBefore(updatedRental.getStartDate())) {

            throw new RuntimeException(
                    "End date cannot be before start date");
        }

        rental.setUserId(
                updatedRental.getUserId());

        rental.setEquipmentId(
                updatedRental.getEquipmentId());

        rental.setStartDate(
                updatedRental.getStartDate());

        rental.setEndDate(
                updatedRental.getEndDate());

        rental.setTotalAmount(
                updatedRental.getTotalAmount());

        rental.setStatus(
                updatedRental.getStatus());

        return repository.save(rental);
    }


    // ==========================================
    // APPROVE RENTAL
    // ==========================================

    @PutMapping("/approve/{id}")
    public Rental approveRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        Equipment equipment =
                equipmentRepository
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

        Rental savedRental =
                repository.save(rental);


        // Send approval email to farmer

        userRepository.findById(
                        rental.getUserId())
                .ifPresentOrElse(

                        farmer -> {

                            try {

                                emailService.sendBookingApprovedEmail(
                                        farmer.getEmail(),
                                        farmer.getName(),
                                        savedRental.getId(),
                                        equipment.getName(),
                                        savedRental.getStartDate(),
                                        savedRental.getEndDate(),
                                        savedRental.getTotalAmount()
                                );

                                System.out.println(
                                        "Booking approval email sent successfully");

                            } catch (Exception e) {

                                System.out.println(
                                        "Booking approval email failed: "
                                                + e.getMessage());
                            }
                        },

                        () -> {

                            System.out.println(
                                    "FARMER NOT FOUND FOR USER ID: "
                                            + rental.getUserId());
                        }
                );

        return savedRental;
    }


    // ==========================================
    // REJECT RENTAL
    // ==========================================

    @PutMapping("/reject/{id}")
    public Rental rejectRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        rental.setStatus("REJECTED");

        Rental savedRental =
                repository.save(rental);

        Equipment equipment =
                equipmentRepository
                        .findById(rental.getEquipmentId())
                        .orElse(null);


        // Send rejection email to farmer

        userRepository.findById(
                        rental.getUserId())
                .ifPresentOrElse(

                        farmer -> {

                            try {

                                String equipmentName =
                                        equipment != null
                                                ? equipment.getName()
                                                : "Agricultural Equipment";

                                emailService.sendBookingRejectedEmail(
                                        farmer.getEmail(),
                                        farmer.getName(),
                                        savedRental.getId(),
                                        equipmentName,
                                        savedRental.getStartDate(),
                                        savedRental.getEndDate(),
                                        savedRental.getTotalAmount()
                                );

                                System.out.println(
                                        "Booking rejection email sent successfully");

                            } catch (Exception e) {

                                System.out.println(
                                        "Booking rejection email failed: "
                                                + e.getMessage());
                            }
                        },

                        () -> {

                            System.out.println(
                                    "FARMER NOT FOUND FOR USER ID: "
                                            + rental.getUserId());
                        }
                );

        return savedRental;
    }


    // ==========================================
    // COMPLETE RENTAL
    // ==========================================

    @PutMapping("/complete/{id}")
    public Rental completeRental(
            @PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found with id: " + id));

        Equipment equipment =
                equipmentRepository
                        .findById(rental.getEquipmentId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Equipment not found with id: "
                                                + rental.getEquipmentId()));

        rental.setStatus("COMPLETED");

        equipment.setAvailable(true);

        equipmentRepository.save(equipment);

        return repository.save(rental);
    }


    // ==========================================
    // DELETE RENTAL
    // ==========================================

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