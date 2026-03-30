package com.example.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String notes;
    private BigDecimal totalCost;

    @OneToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @ManyToMany
    @JoinTable(
        name = "prescription_medicine",
        joinColumns = @JoinColumn(name = "prescription_id"),
        inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )
    private List<Medicine> medicines;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }

    public List<Medicine> getMedicines() { return medicines; }
    public void setMedicines(List<Medicine> medicines) { this.medicines = medicines; }
}