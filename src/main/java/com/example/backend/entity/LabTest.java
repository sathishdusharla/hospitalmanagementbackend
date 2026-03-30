package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LabTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testName;
    private String instructions;
    private String status; // PENDING, COMPLETED
    private String resultNotes;
    private String reportFileName;
    private LocalDateTime orderedAt;
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "consultation_id")
    @JsonIgnoreProperties({"labTests", "doctor", "patient"})
    private Consultation consultation;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResultNotes() { return resultNotes; }
    public void setResultNotes(String resultNotes) { this.resultNotes = resultNotes; }

    public String getReportFileName() { return reportFileName; }
    public void setReportFileName(String reportFileName) { this.reportFileName = reportFileName; }

    public LocalDateTime getOrderedAt() { return orderedAt; }
    public void setOrderedAt(LocalDateTime orderedAt) { this.orderedAt = orderedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}
