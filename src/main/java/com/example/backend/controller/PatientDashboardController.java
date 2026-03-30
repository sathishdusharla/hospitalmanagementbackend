package com.example.backend.controller;

import com.example.backend.dto.AppointmentRequest;
import com.example.backend.dto.ComplaintRequest;
import com.example.backend.repository.ConsultationRepository;
import com.example.backend.repository.PrescriptionRepository;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.ComplaintService;
import com.example.backend.service.DashboardService;
import com.example.backend.service.LabTestService;
import com.example.backend.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private MappingService mappingService;

    @GetMapping("/{patientId}/dashboard")
    public ResponseEntity<?> overview(@PathVariable Long patientId) {
        return ResponseEntity.ok(dashboardService.patientOverview(patientId));
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            return ResponseEntity.ok(appointmentService.book(request));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/{patientId}/appointments")
    public ResponseEntity<?> myAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getPatientAppointments(patientId)));
    }

    @GetMapping("/{patientId}/prescriptions")
    public ResponseEntity<?> myPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionRepository.findByConsultation_Patient_IdOrderByIdDesc(patientId));
    }

    @GetMapping("/{patientId}/dashboard-history")
    public ResponseEntity<?> myHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(Map.of(
            "consultations", consultationRepository.findByPatient_IdOrderByDateTimeDesc(patientId),
            "prescriptions", prescriptionRepository.findByConsultation_Patient_IdOrderByIdDesc(patientId)
        ));
    }

    @PostMapping("/complaints")
    public ResponseEntity<?> reportOvercharging(@RequestBody ComplaintRequest request) {
        try {
            return ResponseEntity.ok(complaintService.createComplaint(request));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/{patientId}/lab-tests")
    public ResponseEntity<?> getLabTests(@PathVariable Long patientId) {
        return ResponseEntity.ok(labTestService.getByPatient(patientId));
    }

    @PostMapping("/lab-tests/{labTestId}/upload")
    public ResponseEntity<?> uploadLabTestReport(
        @PathVariable Long labTestId,
        @RequestParam("file") MultipartFile file
    ) {
        try {
            return ResponseEntity.ok(labTestService.uploadReport(labTestId, file));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
