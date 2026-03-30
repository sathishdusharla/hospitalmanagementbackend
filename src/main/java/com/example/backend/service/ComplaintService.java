package com.example.backend.service;

import com.example.backend.dto.ComplaintRequest;
import com.example.backend.entity.Complaint;
import com.example.backend.entity.Consultation;
import com.example.backend.entity.Patient;
import com.example.backend.entity.Prescription;
import com.example.backend.repository.ComplaintRepository;
import com.example.backend.repository.ConsultationRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public Complaint createComplaint(ComplaintRequest request) throws Exception {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new Exception("Patient not found"));

        Complaint complaint = new Complaint();
        complaint.setPatient(patient);
        complaint.setComplaintType(request.getComplaintType());
        complaint.setDescription(request.getDescription());
        complaint.setReportedAmount(request.getReportedAmount());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus("OPEN");

        if (request.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(request.getConsultationId())
                .orElseThrow(() -> new Exception("Consultation not found"));
            complaint.setConsultation(consultation);
            complaint.setDoctor(consultation.getDoctor());
        }
        if (request.getPrescriptionId() != null) {
            Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new Exception("Prescription not found"));
            complaint.setPrescription(prescription);
            if (prescription.getConsultation() != null) {
                complaint.setDoctor(prescription.getConsultation().getDoctor());
            }
        }

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint markResolved(Long complaintId) throws Exception {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new Exception("Complaint not found"));
        complaint.setStatus("RESOLVED");
        return complaintRepository.save(complaint);
    }
}
