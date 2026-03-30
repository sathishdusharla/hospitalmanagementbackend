package com.example.backend.controller;

import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.MappingService;
import com.example.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MappingService mappingService;

    /**
     * Get all recent appointments (for all portals to sync)
     */
    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments() {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentRepository.findAll()));
    }

    /**
     * Get appointments for a specific doctor (Doctor portal sync)
     */
    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getDoctorAppointments(doctorId)));
    }

    /**
     * Get appointments for a specific patient (Patient portal sync)
     */
    @GetMapping("/appointments/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getPatientAppointments(patientId)));
    }

    /**
     * Get all patients (Admin portal sync)
     */
    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatients() {
        return ResponseEntity.ok(mappingService.toPatientResponseList(patientService.getAllPatients()));
    }

    /**
     * Get a specific patient by ID
     */
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> getPatient(@PathVariable Long patientId) {
        return patientRepository.findById(patientId)
            .map(patient -> ResponseEntity.ok(mappingService.toPatientResponse(patient)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get appointment status (for real-time updates)
     */
    @GetMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<?> getAppointmentStatus(@PathVariable Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
            .map(appointment -> {
                Map<String, Object> response = new HashMap<>();
                response.put("id", appointment.getId());
                response.put("status", appointment.getStatus());
                response.put("appointmentDateTime", appointment.getAppointmentDateTime());
                response.put("patientName", appointment.getPatient() != null ? appointment.getPatient().getName() : null);
                response.put("doctorName", appointment.getDoctor() != null ? appointment.getDoctor().getName() : null);
                response.put("dayPatientNumber", appointment.getDayPatientNumber());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get today's appointments for a doctor (for queue display)
     */
    @GetMapping("/appointments/doctor/{doctorId}/today")
    public ResponseEntity<?> getTodayAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getDoctorTodayAppointments(doctorId)));
    }

    /**
     * Get summary statistics (for dashboard sync)
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPatients", patientRepository.count());
        summary.put("totalAppointments", appointmentRepository.count());
        summary.put("todayAppointments", appointmentRepository.countByAppointmentDateTimeBetween(
            LocalDateTime.now().toLocalDate().atStartOfDay(),
            LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1)
        ));
        summary.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(summary);
    }
}
