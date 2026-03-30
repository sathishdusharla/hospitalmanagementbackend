package com.example.backend.service;

import com.example.backend.entity.Alert;
import com.example.backend.repository.AlertRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public Alert createAlert(String type, String message, String severity, Long doctorId, Long patientId, Long prescriptionId) {
        Alert alert = new Alert();
        alert.setDateTime(LocalDateTime.now());
        alert.setType(type);
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setReviewed(false);
        if (doctorId != null) {
            doctorRepository.findById(doctorId).ifPresent(alert::setDoctor);
        }
        if (patientId != null) {
            patientRepository.findById(patientId).ifPresent(alert::setPatient);
        }
        if (prescriptionId != null) {
            prescriptionRepository.findById(prescriptionId).ifPresent(alert::setPrescription);
        }
        return alertRepository.save(alert);
    }

    public List<Alert> getUnreviewedAlerts() {
        return alertRepository.findByReviewedFalse();
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public void markAsReviewed(Long alertId) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setReviewed(true);
            alertRepository.save(alert);
        }
    }
}