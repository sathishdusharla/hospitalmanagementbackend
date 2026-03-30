package com.example.backend.service;

import com.example.backend.entity.MedicalHistory;
import com.example.backend.entity.Patient;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.MedicalHistoryRepository;
import com.example.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public MedicalHistory addToHistory(Long patientId, String diagnosis, String prescribedMedicines, String notes, Long doctorId) throws Exception {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new Exception("Patient not found"));

        MedicalHistory history = new MedicalHistory();
        history.setPatient(patient);
        history.setDiagnosis(diagnosis);
        history.setPrescribedMedicines(prescribedMedicines);
        history.setNotes(notes);
        history.setDateTime(LocalDateTime.now());

        if (doctorId != null) {
            doctorRepository.findById(doctorId).ifPresent(history::setDoctor);
        }

        return medicalHistoryRepository.save(history);
    }

    public List<MedicalHistory> getPatientHistory(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            return List.of();
        }
        return medicalHistoryRepository.findByPatient_Id(patientId);
    }
}
