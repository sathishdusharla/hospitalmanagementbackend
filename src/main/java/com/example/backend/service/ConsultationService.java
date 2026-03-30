package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AlertService alertService;

    private static final BigDecimal STANDARD_FEE = new BigDecimal("200.00");
    private static final BigDecimal MAX_CONSULTATION_FEE = new BigDecimal("500.00");

    public Consultation createConsultation(Long doctorId, Long patientId, String symptoms, String diagnosis, BigDecimal fee) throws Exception {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new Exception("Doctor not found"));
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new Exception("Patient not found"));

        // Fire a single alert scaled to severity — avoids duplicate alerts for the same fee
        if (fee != null) {
            if (fee.compareTo(MAX_CONSULTATION_FEE) > 0) {
                alertService.createAlert("OVERCHARGING",
                    "Consultation fee " + fee + " exceeds maximum allowed " + MAX_CONSULTATION_FEE,
                    "HIGH", doctorId, patientId, null);
            } else if (fee.compareTo(STANDARD_FEE) > 0) {
                alertService.createAlert("OVERCHARGING",
                    "Consultation fee " + fee + " exceeds standard fee " + STANDARD_FEE,
                    "MEDIUM", doctorId, patientId, null);
            }
        }

        Consultation consultation = new Consultation();
        consultation.setDoctor(doctor);
        consultation.setPatient(patient);
        consultation.setDateTime(LocalDateTime.now());
        consultation.setSymptoms(symptoms);
        consultation.setDiagnosis(diagnosis);
        consultation.setConsultationFee(fee);
        consultation.setStandardFee(STANDARD_FEE);

        return consultationRepository.save(consultation);
    }

    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    public Optional<Consultation> getConsultationById(Long id) {
        return consultationRepository.findById(id);
    }
}
