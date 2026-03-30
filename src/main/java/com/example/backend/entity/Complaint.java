package com.example.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String complaintType;
    private String description;
    private BigDecimal reportedAmount;
    private String status;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getReportedAmount() { return reportedAmount; }
    public void setReportedAmount(BigDecimal reportedAmount) { this.reportedAmount = reportedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }

    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }
}
