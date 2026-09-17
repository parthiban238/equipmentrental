package equipmentrental.controller;

import equipmentrental.entity.Equipment;
import equipmentrental.repository.EquipmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(
        origins = {
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        },
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
public class EquipmentController {

    private final EquipmentRepository repository;

    public EquipmentController(EquipmentRepository repository) {
        this.repository = repository;
    }

    // Get all equipment
    @GetMapping
    public List<Equipment> getAllEquipment() {
        return repository.findAll();
    }

    // Get available equipment
    @GetMapping("/available")
    public List<Equipment> getAvailableEquipment() {
        return repository.findByAvailableTrue();
    }

    // Get equipment by owner
    @GetMapping("/owner/{ownerId}")
    public List<Equipment> getEquipmentByOwner(
            @PathVariable Long ownerId) {

        return repository.findByOwnerId(ownerId);
    }

    // Get equipment by category
    @GetMapping("/category/{category}")
    public List<Equipment> getEquipmentByCategory(
            @PathVariable String category) {

        return repository.findByCategory(category);
    }

    // Get equipment by location
    @GetMapping("/location/{location}")
    public List<Equipment> getEquipmentByLocation(
            @PathVariable String location) {

        return repository.findByLocation(location);
    }

    // Add equipment
    @PostMapping
    public Equipment addEquipment(
            @RequestBody Equipment equipment) {

        return repository.save(equipment);
    }

    // Get equipment by ID
    @GetMapping("/id/{id}")
    public Equipment getEquipmentById(
            @PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: " + id));
    }

    // Update equipment
    @PutMapping("/id/{id}")
    public Equipment updateEquipment(
            @PathVariable Long id,
            @RequestBody Equipment updatedEquipment) {

        Equipment equipment = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: " + id));

        equipment.setName(updatedEquipment.getName());

        equipment.setCategory(
                updatedEquipment.getCategory());

        equipment.setPricePerDay(
                updatedEquipment.getPricePerDay());

        equipment.setLocation(
                updatedEquipment.getLocation());

        equipment.setAvailable(
                updatedEquipment.isAvailable());

        // Update owner ID
        equipment.setOwnerId(
                updatedEquipment.getOwnerId());

        return repository.save(equipment);
    }

    // Update equipment availability
    @PutMapping("/availability/{id}")
    public Equipment updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {

        Equipment equipment = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: " + id));

        equipment.setAvailable(available);

        return repository.save(equipment);
    }

    // Delete equipment
    @DeleteMapping("/{id}")
    public String deleteEquipment(
            @PathVariable Long id) {

        Equipment equipment = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found with id: " + id));

        repository.delete(equipment);

        return "Equipment deleted successfully";
    }
}