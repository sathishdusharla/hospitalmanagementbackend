package com.example.backend.controller;

import com.example.backend.dto.AdminCreatePatientRequest;
import com.example.backend.dto.AppointmentRequest;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Hospital;
import com.example.backend.entity.Patient;
import com.example.backend.repository.AlertRepository;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.HospitalRepository;
import com.example.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private HospitalRepository appHospitalRepository;

    @Autowired
    private MappingService mappingService;

    @GetMapping("/system-overview")
    public ResponseEntity<?> systemOverview() {
        return ResponseEntity.ok(dashboardService.adminOverview());
    }

    @GetMapping("/hospitals")
    public ResponseEntity<?> hospitals() {
        return ResponseEntity.ok(hospitalRepository.findAll());
    }

    @PutMapping("/hospitals/{hospitalId}/approve")
    public ResponseEntity<?> approveHospital(@PathVariable Long hospitalId) {
        return hospitalRepository.findById(hospitalId)
            .<ResponseEntity<?>>map(h -> {
                h.setApproved(true);
                hospitalRepository.save(h);
                return ResponseEntity.ok(h);
            })
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Hospital not found")));
    }

    @DeleteMapping("/hospitals/{hospitalId}")
    public ResponseEntity<?> removeHospital(@PathVariable Long hospitalId) {
        hospitalRepository.deleteById(hospitalId);
        return ResponseEntity.ok(Map.of("message", "Hospital removed"));
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> doctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    @PutMapping("/doctors/{doctorId}/approve")
    public ResponseEntity<?> approveDoctor(@PathVariable Long doctorId) {
        return doctorRepository.findById(doctorId)
            .<ResponseEntity<?>>map(d -> {
                d.setApproved(true);
                doctorRepository.save(d);
                return ResponseEntity.ok(d);
            })
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Doctor not found")));
    }

    @GetMapping("/alerts")
    public ResponseEntity<?> alerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }

    @PutMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long alertId) {
        return alertRepository.findById(alertId)
            .<ResponseEntity<?>>map(alert -> {
                alert.setReviewed(true);
                alertRepository.save(alert);
                return ResponseEntity.ok(alert);
            })
            .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Alert not found")));
    }

    @GetMapping("/complaints")
    public ResponseEntity<?> complaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @PutMapping("/complaints/{complaintId}/resolve")
    public ResponseEntity<?> resolveComplaint(@PathVariable Long complaintId) {
        try {
            return ResponseEntity.ok(complaintService.markResolved(complaintId));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> analytics() {
        return ResponseEntity.ok(dashboardService.analytics());
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatients() {
        return ResponseEntity.ok(mappingService.toPatientResponseList(patientService.getAllPatients()));
    }

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody AdminCreatePatientRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username is required"));
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
            }

            String username = request.getUsername().trim().toLowerCase();
            if (appUserRepository.findByUsernameIgnoreCase(username).isPresent()) {
                return ResponseEntity.status(409).body(Map.of("message", "Username already exists"));
            }

            Hospital hospital = appHospitalRepository.findById(request.getHospitalId()).orElse(null);

            // Build contact info with email and phone
            String contactInfo = "";
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                contactInfo = request.getEmail();
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                if (!contactInfo.isEmpty()) contactInfo += ", ";
                contactInfo += request.getPhone();
            }

            Patient patient = new Patient();
            patient.setName(request.getFullName());
            patient.setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth().toString() : null);
            patient.setGender(request.getGender());
            patient.setContactInfo(contactInfo.isEmpty() ? null : contactInfo);
            if (hospital != null) patient.setHospital(hospital);
            Patient savedPatient = patientService.createPatient(patient);

            AppUser appUser = new AppUser();
            appUser.setUsername(username);
            appUser.setPassword(passwordEncoder.encode(request.getPassword()));
            appUser.setRole("PATIENT");
            appUser.setFullName(request.getFullName());
            appUser.setLinkedEntityId(savedPatient.getId());
            appUserRepository.save(appUser);

            return ResponseEntity.ok(Map.of(
                "message", "Patient created successfully",
                "patientId", savedPatient.getId(),
                "patient", savedPatient
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/patients/search")
    public ResponseEntity<?> searchPatientByEmail(@RequestParam String email) {
        try {
            List<Patient> patients = patientService.getAllPatients();
            Patient found = patients.stream()
                .filter(p -> p.getContactInfo() != null && p.getContactInfo().toLowerCase().contains(email.toLowerCase()))
                .findFirst()
                .orElse(null);

            if (found == null) {
                return ResponseEntity.ok(Map.of("message", "Patient not found", "patient", (Object) null));
            }

            return ResponseEntity.ok(Map.of("patient", found));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/patients/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable Long patientId) {
        try {
            patientService.deletePatient(patientId);
            return ResponseEntity.ok(Map.of("message", "Patient deleted"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/doctors/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        try {
            doctorService.deleteDoctor(doctorId);
            return ResponseEntity.ok(Map.of("message", "Doctor deleted"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments() {
        return ResponseEntity.ok(mappingService.toAppointmentResponseList(appointmentService.getAllAppointments()));
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            return ResponseEntity.ok(appointmentService.book(request));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
