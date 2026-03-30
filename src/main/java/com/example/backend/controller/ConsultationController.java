package com.example.backend.controller;

import com.example.backend.entity.Consultation;
import com.example.backend.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<Consultation> createConsultation(@RequestParam Long doctorId, @RequestParam Long patientId,
                                                           @RequestParam String symptoms, @RequestParam String diagnosis,
                                                           @RequestParam BigDecimal fee) {
        try {
            return ResponseEntity.ok(consultationService.createConsultation(doctorId, patientId, symptoms, diagnosis, fee));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Consultation>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultation> getConsultationById(@PathVariable Long id) {
        return consultationService.getConsultationById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}