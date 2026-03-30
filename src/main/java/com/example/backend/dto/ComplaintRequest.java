package com.example.backend.dto;

import java.math.BigDecimal;

public class ComplaintRequest {
    private Long patientId;
    private Long consultationId;
    private Long prescriptionId;
    private String complaintType;
    private String description;
    private BigDecimal reportedAmount;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }

    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getReportedAmount() { return reportedAmount; }
    public void setReportedAmount(BigDecimal reportedAmount) { this.reportedAmount = reportedAmount; }
}
