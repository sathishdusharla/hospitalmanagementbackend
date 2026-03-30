package com.example.backend.service;

import com.example.backend.entity.Patient;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient updatePatient(Long id, Patient patientDetails) throws Exception {
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (!patientOpt.isPresent()) {
            throw new Exception("Patient not found");
        }
        Patient patient = patientOpt.get();
        patient.setName(patientDetails.getName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setGender(patientDetails.getGender());
        patient.setContactInfo(patientDetails.getContactInfo());
        patient.setHospital(patientDetails.getHospital());
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id) throws Exception {
        if (!patientRepository.existsById(id)) {
            throw new Exception("Patient not found");
        }
        appointmentRepository.nullifyConsultationByPatient_Id(id);
        appointmentRepository.deleteByPatient_Id(id);
        patientRepository.deleteById(id);
        appUserRepository.findByLinkedEntityId(id).ifPresent(appUserRepository::delete);
    }
}