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

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return repository.findAll();
    }

    @PostMapping
    public Equipment addEquipment(@RequestBody Equipment equipment) {
        return repository.save(equipment);
    }
}