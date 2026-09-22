package equipmentrental.controller;

import equipmentrental.entity.Rating;
import equipmentrental.entity.Rental;
import equipmentrental.repository.RatingRepository;
import equipmentrental.repository.RentalRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(
    origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "https://equipmentrental-2bit.vercel.app"
    }
)
public class RatingController {

    private final RatingRepository ratingRepository;
    private final RentalRepository rentalRepository;

    public RatingController(
            RatingRepository ratingRepository,
            RentalRepository rentalRepository) {

        this.ratingRepository = ratingRepository;
        this.rentalRepository = rentalRepository;
    }

    // ==========================================
    // Add Rating & Review
    // ==========================================

    @PostMapping
    public ResponseEntity<?> addRating(
            @RequestBody Rating rating) {

        try {

            if (rating.getRentalId() == null) {
                return ResponseEntity.badRequest()
                        .body("Rental ID is required.");
            }

            if (rating.getUserId() == null) {
                return ResponseEntity.badRequest()
                        .body("User ID is required.");
            }

            if (rating.getEquipmentId() == null) {
                return ResponseEntity.badRequest()
                        .body("Equipment ID is required.");
            }

            // Rating must be between 1 and 5
            if (rating.getRating() < 1 ||
                rating.getRating() > 5) {

                return ResponseEntity.badRequest()
                        .body("Rating must be between 1 and 5.");
            }

            // Check rental
            Rental rental =
                    rentalRepository
                            .findById(rating.getRentalId())
                            .orElse(null);

            if (rental == null) {
                return ResponseEntity.badRequest()
                        .body("Rental not found.");
            }

            // Only COMPLETED rental can be reviewed
            if (!"COMPLETED".equalsIgnoreCase(
                    rental.getStatus())) {

                return ResponseEntity.badRequest()
                        .body(
                            "Rating is allowed only for completed rentals."
                        );
            }

            // Check user owns this rental
            if (!rental.getUserId()
                    .equals(rating.getUserId())) {

                return ResponseEntity.badRequest()
                        .body(
                            "You cannot review this rental."
                        );
            }

            // Check equipment matches
            if (!rental.getEquipmentId()
                    .equals(rating.getEquipmentId())) {

                return ResponseEntity.badRequest()
                        .body(
                            "Equipment does not match the rental."
                        );
            }

            // Prevent duplicate review
            if (ratingRepository
                    .existsByRentalId(
                        rating.getRentalId())) {

                return ResponseEntity.badRequest()
                        .body(
                            "You have already reviewed this rental."
                        );
            }

            Rating saved =
                    ratingRepository.save(rating);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(
                        "Failed to save rating: "
                        + e.getMessage()
                    );
        }
    }

    // ==========================================
    // Get reviews for equipment
    // ==========================================

    @GetMapping("/equipment/{equipmentId}")
    public List<Rating> getEquipmentRatings(
            @PathVariable Long equipmentId) {

        return ratingRepository
                .findByEquipmentId(equipmentId);
    }

    // ==========================================
    // Get user's ratings
    // ==========================================

    @GetMapping("/user/{userId}")
    public List<Rating> getUserRatings(
            @PathVariable Long userId) {

        return ratingRepository
                .findByUserId(userId);
    }

    // ==========================================
    // Get rating by rental
    // ==========================================

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<?> getRentalRating(
            @PathVariable Long rentalId) {

        return ratingRepository
                .findByRentalId(rentalId)
                .map(ResponseEntity::ok)
                .orElse(
                    ResponseEntity.notFound().build()
                );
    }
}