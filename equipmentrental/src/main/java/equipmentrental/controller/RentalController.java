package equipmentrental.controller;

import equipmentrental.entity.Rental;
import equipmentrental.repository.RentalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalRepository repository;

    public RentalController(RentalRepository repository) {
        this.repository = repository;
    }

    // Get all rentals
    @GetMapping
    public List<Rental> getAllRentals() {
        return repository.findAll();
    }

    // Create rental
    @PostMapping
    public Rental createRental(@RequestBody Rental rental) {
        return repository.save(rental);
    }

    // Get rental by ID
    @GetMapping("/id/{id}")
    public Rental getRentalById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rental not found with id: " + id));
    }

    // Update rental
    @PutMapping("/id/{id}")
    public Rental updateRental(
            @PathVariable Long id,
            @RequestBody Rental updatedRental) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rental not found with id: " + id));

        rental.setUserId(updatedRental.getUserId());
        rental.setEquipmentId(updatedRental.getEquipmentId());
        rental.setStartDate(updatedRental.getStartDate());
        rental.setEndDate(updatedRental.getEndDate());
        rental.setTotalAmount(updatedRental.getTotalAmount());
        rental.setStatus(updatedRental.getStatus());

        return repository.save(rental);
    }

    // Delete rental
    @DeleteMapping("/id/{id}")
    public String deleteRental(@PathVariable Long id) {

        Rental rental = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rental not found with id: " + id));

        repository.delete(rental);

        return "Rental deleted successfully";
    }
}