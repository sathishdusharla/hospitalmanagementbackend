package com.example.backend.controller;

import com.example.backend.auth.JwtUtil;
import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.AppUser;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Hospital;
import com.example.backend.entity.Patient;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.HospitalRepository;
import com.example.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Set<String> ALLOWED_ROLES = Set.of("PATIENT", "DOCTOR");

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String normalizedUsername = request.getUsername() == null ? "" : request.getUsername().trim().toLowerCase();
        String normalizedRole = request.getRole() == null ? "PATIENT" : request.getRole().trim().toUpperCase();

        if (normalizedUsername.isBlank() || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required"));
        }

        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Role must be PATIENT or DOCTOR"));
        }

        if (appUserRepository.findByUsernameIgnoreCase(normalizedUsername).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Username already exists"));
        }

        Optional<Hospital> hospitalOpt = request.getHospitalId() == null
            ? Optional.empty()
            : hospitalRepository.findById(request.getHospitalId());

        if (request.getHospitalId() != null && hospitalOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Selected hospital not found"));
        }

        if ("DOCTOR".equals(normalizedRole)) {
            if (request.getSpecialization() == null || request.getSpecialization().isBlank()
                    || request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor specialization and license number are required"));
            }
            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor hospital is required"));
            }
        }

        AppUser user = new AppUser();
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(normalizedRole);
        user.setFullName(request.getFullName());
        user = appUserRepository.save(user);

        if ("PATIENT".equals(normalizedRole)) {
            Patient patient = new Patient();
            patient.setName(request.getFullName());
            patient.setDateOfBirth(request.getDateOfBirth());
            patient.setGender(request.getGender());
            patient.setContactInfo(buildPatientContactInfo(request));
            hospitalOpt.ifPresent(patient::setHospital);
            Patient saved = patientRepository.save(patient);
            user.setLinkedEntityId(saved.getId());
            appUserRepository.save(user);
        } else if ("DOCTOR".equals(normalizedRole)) {
            Doctor doctor = new Doctor();
            doctor.setName(request.getFullName());
            doctor.setSpecialization(request.getSpecialization());
            doctor.setLicenseNumber(request.getLicenseNumber());
            doctor.setHospital(hospitalOpt.orElse(null));
            Doctor saved = doctorRepository.save(doctor);
            user.setLinkedEntityId(saved.getId());
            appUserRepository.save(user);
        }

        return ResponseEntity.ok(Map.of(
            "message", "Registration successful",
            "username", normalizedUsername,
            "role", normalizedRole
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String normalizedUsername = request.getUsername() == null ? "" : request.getUsername().trim().toLowerCase();

        if (normalizedUsername.isBlank() || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        Optional<AppUser> registeredUser = appUserRepository.findByUsernameIgnoreCase(normalizedUsername);
        if (registeredUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        AppUser user = registeredUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(normalizedUsername, user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, normalizedUsername, user.getRole(), 86400, user.getLinkedEntityId()));
    }

    private String buildPatientContactInfo(RegisterRequest request) {
        StringBuilder contactInfo = new StringBuilder();

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            contactInfo.append("Phone: ").append(request.getPhone());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!contactInfo.isEmpty()) contactInfo.append(" | ");
            contactInfo.append("Email: ").append(request.getEmail());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            if (!contactInfo.isEmpty()) contactInfo.append(" | ");
            contactInfo.append("Address: ").append(request.getAddress());
        }
        if (request.getEmergencyContact() != null && !request.getEmergencyContact().isBlank()) {
            if (!contactInfo.isEmpty()) contactInfo.append(" | ");
            contactInfo.append("Emergency: ").append(request.getEmergencyContact());
        }

        return contactInfo.toString();
    }
}
