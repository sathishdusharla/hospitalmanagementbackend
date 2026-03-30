package com.example.backend.dto;

import java.math.BigDecimal;

public class AppointmentRequest {
    private Long patientId;
    private Long hospitalId;
    private Long doctorId;
    private String appointmentDateTime;
    private String symptomsOrDisease;
    private BigDecimal consultationFee;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(String appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getSymptomsOrDisease() { return symptomsOrDisease; }
    public void setSymptomsOrDisease(String symptomsOrDisease) { this.symptomsOrDisease = symptomsOrDisease; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
}
