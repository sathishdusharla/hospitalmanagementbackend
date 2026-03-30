package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentResponse {
    private Long id;
    private LocalDateTime appointmentDateTime;
    private String symptomsOrDisease;
    private String status;
    private BigDecimal consultationFee;
    private Integer dayPatientNumber;

    // Patient details
    private Long patientId;
    private String patientFullName;
    private String patientEmail;
    private String patientPhone;

    // Doctor details
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;

    // Hospital details
    private Long hospitalId;
    private String hospitalName;

    // Consultation
    private Long consultationId;

    public AppointmentResponse() {}

    // Getters and Setters
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

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientFullName() { return patientFullName; }
    public void setPatientFullName(String patientFullName) { this.patientFullName = patientFullName; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorSpecialization() { return doctorSpecialization; }
    public void setDoctorSpecialization(String doctorSpecialization) { this.doctorSpecialization = doctorSpecialization; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }
}
