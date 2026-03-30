package com.example.backend.controller;

import com.example.backend.entity.Medicine;
import com.example.backend.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Long id) {
        return medicineService.getMedicineById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
