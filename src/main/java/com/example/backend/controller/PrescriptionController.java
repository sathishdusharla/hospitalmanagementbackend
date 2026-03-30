package com.example.backend.controller;

import com.example.backend.entity.Prescription;
import com.example.backend.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestParam Long consultationId,
                                                           @RequestParam List<Long> medicineIds,
                                                           @RequestParam String notes) {
        try {
            return ResponseEntity.ok(prescriptionService.createPrescription(consultationId, medicineIds, notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Long id) {
        return prescriptionService.getPrescriptionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}