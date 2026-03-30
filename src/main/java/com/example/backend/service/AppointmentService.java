package com.example.backend.service;

import com.example.backend.dto.AppointmentRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.Consultation;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Hospital;
import com.example.backend.entity.Patient;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.ConsultationRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.HospitalRepository;
import com.example.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    private static final BigDecimal DEFAULT_CONSULTATION_FEE = new BigDecimal("200.00");

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    public Appointment book(AppointmentRequest request) throws Exception {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new Exception("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new Exception("Doctor not found"));
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
            .orElseThrow(() -> new Exception("Hospital not found"));
        LocalDateTime appointmentDateTime = parseAppointmentDateTime(request.getAppointmentDateTime());

        if (appointmentRepository.existsByPatient_IdAndDoctor_IdAndAppointmentDateTime(
            patient.getId(),
            doctor.getId(),
            appointmentDateTime
        )) {
            throw new Exception("This appointment already exists for the selected patient, doctor, and date-time.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setHospital(hospital);
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setSymptomsOrDisease(request.getSymptomsOrDisease());
        appointment.setConsultationFee(request.getConsultationFee() == null ? DEFAULT_CONSULTATION_FEE : request.getConsultationFee());
        appointment.setDayPatientNumber(calculateNextDayPatientNumber(appointmentDateTime));
        appointment.setStatus("PENDING");
        return appointmentRepository.save(appointment);
    }

    public long getNextDayPatientNumber(String appointmentDateTimeValue) throws Exception {
        LocalDateTime appointmentDateTime = parseAppointmentDateTime(appointmentDateTimeValue);
        return calculateNextDayPatientNumber(appointmentDateTime);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatient_IdOrderByAppointmentDateTimeDesc(patientId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getDoctorTodayAppointments(Long doctorId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return appointmentRepository.findByDoctor_IdAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(doctorId, start, end);
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctor_IdOrderByAppointmentDateTimeAsc(doctorId);
    }

    public Appointment updateStatus(Long appointmentId, String status) throws Exception {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new Exception("Appointment not found"));
        appointment.setStatus(status == null ? appointment.getStatus() : status.trim().toUpperCase());
        return appointmentRepository.save(appointment);
    }

    public Map<String, Object> treatAppointment(Long appointmentId) throws Exception {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new Exception("Appointment not found"));

        Consultation consultation = appointment.getConsultation();
        if (consultation == null) {
            consultation = new Consultation();
            consultation.setDoctor(appointment.getDoctor());
            consultation.setPatient(appointment.getPatient());
            consultation.setDateTime(appointment.getAppointmentDateTime() == null ? LocalDateTime.now() : appointment.getAppointmentDateTime());
            consultation.setSymptoms(appointment.getSymptomsOrDisease());
            consultation.setDiagnosis(null);
            consultation.setConsultationFee(appointment.getConsultationFee() == null ? DEFAULT_CONSULTATION_FEE : appointment.getConsultationFee());
            consultation.setStandardFee(DEFAULT_CONSULTATION_FEE);
            consultation = consultationRepository.save(consultation);
            appointment.setConsultation(consultation);
        }

        appointment.setStatus("COMPLETED");
        Appointment saved = appointmentRepository.save(appointment);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("appointmentId", saved.getId());
        response.put("status", saved.getStatus());
        response.put("consultationId", saved.getConsultation() == null ? null : saved.getConsultation().getId());
        response.put("patientId", saved.getPatient() == null ? null : saved.getPatient().getId());
        response.put("patientName", saved.getPatient() == null ? null : saved.getPatient().getName());
        return response;
    }

    private int calculateNextDayPatientNumber(LocalDateTime appointmentDateTime) {
        LocalDateTime start = appointmentDateTime.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return (int) appointmentRepository.countByAppointmentDateTimeBetween(start, end) + 1;
    }

    private LocalDateTime parseAppointmentDateTime(String value) throws Exception {
        if (value == null || value.isBlank()) {
            throw new Exception("appointmentDateTime is required");
        }

        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Accept legacy format coming from older UIs: 2026-03-20 10:30:00
        }

        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ignored) {
            // final fallback below
        }

        throw new Exception("Invalid appointmentDateTime format. Use ISO format, e.g., 2026-03-20T10:30:00");
    }
}
