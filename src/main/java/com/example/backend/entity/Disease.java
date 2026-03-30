package com.example.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
        name = "disease_medicine",
        joinColumns = @JoinColumn(name = "disease_id"),
        inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )
    private List<Medicine> recommendedMedicines;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Medicine> getRecommendedMedicines() { return recommendedMedicines; }
    public void setRecommendedMedicines(List<Medicine> recommendedMedicines) { this.recommendedMedicines = recommendedMedicines; }
}