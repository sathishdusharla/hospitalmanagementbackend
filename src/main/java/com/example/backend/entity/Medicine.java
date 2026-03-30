package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal cost;
    private BigDecimal standardPrice;
    private boolean isExpensive;

    @ManyToMany(mappedBy = "recommendedMedicines")
    @JsonIgnore
    private List<Disease> diseases;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getStandardPrice() { return standardPrice; }
    public void setStandardPrice(BigDecimal standardPrice) { this.standardPrice = standardPrice; }

    public boolean isExpensive() { return isExpensive; }
    public void setExpensive(boolean expensive) { isExpensive = expensive; }

    public List<Disease> getDiseases() { return diseases; }
    public void setDiseases(List<Disease> diseases) { this.diseases = diseases; }
}