package com.example.backend.controller;

import com.example.backend.dto.AppointmentRequest;
import com.example.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            return ResponseEntity.ok(appointmentService.book(request));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @GetMapping("/doctor/{doctorId}/today")
    public ResponseEntity<?> getDoctorTodayAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorTodayAppointments(doctorId));
    }

    @GetMapping("/day-patient-number")
    public ResponseEntity<?> getDayPatientNumber(@RequestParam String appointmentDateTime) {
        try {
            return ResponseEntity.ok(Map.of("dayPatientNumber", appointmentService.getNextDayPatientNumber(appointmentDateTime)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long appointmentId, @RequestParam String status) {
        try {
            return ResponseEntity.ok(appointmentService.updateStatus(appointmentId, status));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
