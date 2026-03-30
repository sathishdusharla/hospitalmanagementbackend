package com.example.backend.controller;

import com.example.backend.entity.Alert;
import com.example.backend.entity.Consultation;
import com.example.backend.service.AlertService;
import com.example.backend.service.ConsultationService;
import com.example.backend.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        List<Alert> alerts = alertService.getUnreviewedAlerts();
        List<Consultation> allConsultations = consultationService.getAllConsultations();
        long consultationCount = allConsultations.size();

        BigDecimal totalFee = allConsultations.stream()
            .map(Consultation::getConsultationFee)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgFee = consultationCount == 0
            ? BigDecimal.ZERO
            : totalFee.divide(BigDecimal.valueOf(consultationCount), 2, RoundingMode.HALF_UP);

        Map<String, Object> dashboard = Map.of(
            "unreviewedAlerts", alerts,
            "averageConsultationFee", avgFee,
            "totalConsultations", consultationCount,
            "totalPrescriptions", prescriptionService.getAllPrescriptions().size()
        );
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/alerts/{id}/review")
    public ResponseEntity<Void> reviewAlert(@PathVariable Long id) {
        alertService.markAsReviewed(id);
        return ResponseEntity.ok().build();
    }
}
