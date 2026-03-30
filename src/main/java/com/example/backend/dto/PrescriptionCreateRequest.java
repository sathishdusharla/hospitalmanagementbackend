package com.example.backend.dto;

import java.util.List;

public class PrescriptionCreateRequest {
    private Long consultationId;
    private String notes;
    private List<Long> medicineIds;

    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Long> getMedicineIds() { return medicineIds; }
    public void setMedicineIds(List<Long> medicineIds) { this.medicineIds = medicineIds; }
}
