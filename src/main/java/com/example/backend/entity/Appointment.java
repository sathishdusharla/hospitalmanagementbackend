package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentDateTime;
    private String symptomsOrDisease;
    private String status;
    private BigDecimal consultationFee;
    private Integer dayPatientNumber;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties({"doctors", "staff", "patients"})
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnoreProperties({"hospital", "consultations"})
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"hospital", "consultations"})
    private Patient patient;

    @OneToOne
    @JoinColumn(name = "consultation_id")
    @JsonIgnoreProperties({"doctor", "patient", "prescription"})
    private Consultation consultation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getSymptomsOrDisease() { return symptomsOrDisease; }
    public void setSymptomsOrDisease(String symptomsOrDisease) { this.symptomsOrDisease = symptomsOrDisease; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public Integer getDayPatientNumber() { return dayPatientNumber; }
    public void setDayPatientNumber(Integer dayPatientNumber) { this.dayPatientNumber = dayPatientNumber; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}
