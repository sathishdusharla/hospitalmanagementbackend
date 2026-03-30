package com.example.backend.dto;

public class LabTestRequest {
    private Long consultationId;
    private String testName;
    private String instructions;

    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
