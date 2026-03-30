package com.example.backend.controller;

import com.example.backend.entity.Consultation;
import com.example.backend.entity.MedicalHistory;
import com.example.backend.service.ConsultationService;
import com.example.backend.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientPortalController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<MedicalHistory>> getPatientHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalHistoryService.getPatientHistory(patientId));
    }

    @GetMapping("/{patientId}/consultations")
    public ResponseEntity<List<Consultation>> getPatientConsultations(@PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.getAllConsultations().stream()
            .filter(c -> c.getPatient().getId().equals(patientId))
            .toList());
    }

    @GetMapping("/{patientId}/details")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable Long patientId) {
        List<MedicalHistory> history = medicalHistoryService.getPatientHistory(patientId);
        List<Consultation> consultations = consultationService.getAllConsultations().stream()
            .filter(c -> c.getPatient().getId().equals(patientId))
            .toList();

        Map<String, Object> details = Map.of(
            "medicalHistory", history,
            "consultations", consultations
        );
        return ResponseEntity.ok(details);
    }
}