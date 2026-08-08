package equipmentrental.controller;

import equipmentrental.entity.Equipment;
import equipmentrental.repository.EquipmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
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

    // Add equipment
    @PostMapping
    public Equipment addEquipment(@RequestBody Equipment equipment) {
        return repository.save(equipment);
    }

    // Get equipment by ID
    @GetMapping("/id/{id}")
    public Equipment getEquipmentById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Equipment not found with id: " + id));
    }

    // Update equipment
    @PutMapping("/id/{id}")
    public Equipment updateEquipment(
            @PathVariable Long id,
            @RequestBody Equipment updatedEquipment) {

        Equipment equipment = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Equipment not found with id: " + id));

        equipment.setName(updatedEquipment.getName());
        equipment.setCategory(updatedEquipment.getCategory());
        equipment.setPricePerDay(updatedEquipment.getPricePerDay());
        equipment.setLocation(updatedEquipment.getLocation());
        equipment.setAvailable(updatedEquipment.isAvailable());

        return repository.save(equipment);
    }

    // Delete equipment
    @DeleteMapping("/id/{id}")
    public String deleteEquipment(@PathVariable Long id) {

        Equipment equipment = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Equipment not found with id: " + id));

        repository.delete(equipment);

        return "Equipment deleted successfully";
    }
}