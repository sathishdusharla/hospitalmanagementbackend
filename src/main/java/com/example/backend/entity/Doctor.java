package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    private String licenseNumber;
    private boolean approved = false;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties({"doctors", "consultations", "patients"})
    private Hospital hospital;

    @JsonIgnoreProperties({"doctor", "patient", "hospital", "prescription"})
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Consultation> consultations;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public List<Consultation> getConsultations() { return consultations; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }
}