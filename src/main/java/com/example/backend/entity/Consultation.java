package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;
    private String symptoms;
    private String diagnosis;
    private String clinicalNotes;
    private BigDecimal consultationFee;
    private BigDecimal standardFee;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnoreProperties({"consultations", "hospital"})
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"consultations", "hospital"})
    private Patient patient;

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL)
    private Prescription prescription;

    @JsonIgnore
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabTest> labTests;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public BigDecimal getStandardFee() { return standardFee; }
    public void setStandardFee(BigDecimal standardFee) { this.standardFee = standardFee; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }

    public List<LabTest> getLabTests() { return labTests; }
    public void setLabTests(List<LabTest> labTests) { this.labTests = labTests; }
}
