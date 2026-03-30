package com.example.backend.controller;

import com.example.backend.dto.PrescriptionCreateRequest;
import com.example.backend.dto.LabTestRequest;
import com.example.backend.entity.Consultation;
import com.example.backend.repository.ConsultationRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.DashboardService;
import com.example.backend.service.MappingService;
import com.example.backend.service.PrescriptionService;
import com.example.backend.service.LabTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private MappingService mappingService;

    @GetMapping("/{doctorId}/dashboard")
    public ResponseEntity<?> overview(@PathVariable Long doctorId) {
        return ResponseEntity.ok(dashboardService.doctorOverview(doctorId));
    }

    @GetMapping("/{doctorId}/appointments/today")
    public ResponseEntity<?> todayAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getDoctorTodayAppointments(doctorId)));
    }

    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<?> allAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getDoctorAppointments(doctorId)));
    }

    @GetMapping("/{doctorId}/treated-patients")
    public ResponseEntity<?> treatedPatientsToday(@PathVariable Long doctorId) {
        List<Map<String, Object>> rows = appointmentService.getDoctorAppointments(doctorId).stream()
            .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
            .map(a -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("appointmentId", a.getId());
                row.put("consultationId", a.getConsultation() == null ? null : a.getConsultation().getId());
                row.put("dateTime", a.getAppointmentDateTime());
                row.put("diagnosis", a.getConsultation() == null ? null : a.getConsultation().getDiagnosis());
                row.put("symptoms", a.getSymptomsOrDisease());
                row.put("hasPrescription", a.getConsultation() != null && a.getConsultation().getPrescription() != null);
                row.put("patientId", a.getPatient() == null ? null : a.getPatient().getId());
                row.put("patientName", a.getPatient() == null ? null : a.getPatient().getName());
                row.put("hospitalName", a.getHospital() == null ? null : a.getHospital().getName());
                return row;
            })
            .toList();
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/appointments/{appointmentId}/treat")
    public ResponseEntity<?> treatAppointment(@PathVariable Long appointmentId) {
        try {
            return ResponseEntity.ok(appointmentService.treatAppointment(appointmentId));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/{doctorId}/previous-treated-patients")
    public ResponseEntity<?> previousTreatedPatients(@PathVariable Long doctorId) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> rows = consultationRepository.findByDoctor_IdOrderByDateTimeDesc(doctorId).stream()
            .filter(c -> c.getDateTime() != null)
            .filter(c -> c.getDateTime().toLocalDate().isBefore(today))
            .filter(c -> c.getDiagnosis() != null && !c.getDiagnosis().isBlank())
            .map(this::toConsultationRow)
            .toList();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/{doctorId}/prescription-eligible-consultations")
    public ResponseEntity<?> prescriptionEligibleConsultations(@PathVariable Long doctorId) {
        List<Map<String, Object>> rows = consultationRepository.findByDoctor_IdOrderByDateTimeDesc(doctorId).stream()
            .filter(c -> c.getDiagnosis() != null && !c.getDiagnosis().isBlank())
            .filter(c -> c.getPrescription() == null)
            .map(this::toConsultationRow)
            .toList();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> patientDetails(@PathVariable Long patientId) {
        return patientRepository.findById(patientId)
            .<ResponseEntity<?>>map(patient -> ResponseEntity.ok(Map.of(
                "patient", patient,
                "medicalHistory", patient.getConsultations() == null ? java.util.List.of() : patient.getConsultations()
            )))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/consultations/{consultationId}/diagnosis")
    public ResponseEntity<?> addDiagnosis(
        @PathVariable Long consultationId,
        @RequestBody Map<String, String> body
    ) {
        String diagnosis = body.get("diagnosis");
        String notes = body.get("notes");

        if (diagnosis == null || diagnosis.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Diagnosis is required"));
        }

        return consultationRepository.findById(consultationId)
            .<ResponseEntity<?>>map(consultation -> {
                consultation.setDiagnosis(diagnosis);
                if (notes != null && !notes.isBlank()) {
                    consultation.setClinicalNotes(notes);
                }
                Consultation saved = consultationRepository.save(consultation);
                return ResponseEntity.ok(saved);
            })
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Consultation not found")));
    }

    @PostMapping("/prescriptions")
    public ResponseEntity<?> addPrescription(@RequestBody PrescriptionCreateRequest request) {
        try {
            return ResponseEntity.ok(
                prescriptionService.createPrescription(
                    request.getConsultationId(),
                    request.getMedicineIds(),
                    request.getNotes()
                )
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/lab-tests")
    public ResponseEntity<?> orderLabTest(@RequestBody LabTestRequest request) {
        try {
            return ResponseEntity.ok(labTestService.orderTest(request.getConsultationId(), request.getTestName(), request.getInstructions()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/consultations/{consultationId}/lab-tests")
    public ResponseEntity<?> getLabTestsForConsultation(@PathVariable Long consultationId) {
        return ResponseEntity.ok(labTestService.getByConsultation(consultationId));
    }

    private Map<String, Object> toConsultationRow(Consultation c) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("consultationId", c.getId());
        row.put("dateTime", c.getDateTime());
        row.put("diagnosis", c.getDiagnosis());
        row.put("clinicalNotes", c.getClinicalNotes());
        row.put("symptoms", c.getSymptoms());
        row.put("hasPrescription", c.getPrescription() != null);
        row.put("hasLabTests", c.getLabTests() != null && !c.getLabTests().isEmpty());

        if (c.getPatient() != null) {
            row.put("patientId", c.getPatient().getId());
            row.put("patientName", c.getPatient().getName());
            row.put("patientGender", c.getPatient().getGender());
        }

        if (c.getDoctor() != null && c.getDoctor().getHospital() != null) {
            row.put("hospitalName", c.getDoctor().getHospital().getName());
        }

        return row;
    }
}
