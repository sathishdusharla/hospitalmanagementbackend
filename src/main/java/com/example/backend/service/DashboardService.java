package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    public Map<String, Object> patientOverview(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatient_IdOrderByAppointmentDateTimeDesc(patientId);
        List<Consultation> consultations = consultationRepository.findByPatient_IdOrderByDateTimeDesc(patientId);
        List<Prescription> prescriptions = prescriptionRepository.findByConsultation_Patient_IdOrderByIdDesc(patientId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("upcomingAppointment", appointments.stream().filter(a -> a.getAppointmentDateTime().toLocalDate().isAfter(LocalDate.now().minusDays(1))).findFirst().orElse(null));
        response.put("lastPrescription", prescriptions.stream().findFirst().orElse(null));
        response.put("totalConsultations", consultations.size());
        response.put("notifications", List.of(
            "Your appointment reminder is active",
            "Review your latest prescription details"
        ));
        return response;
    }

    public Map<String, Object> doctorOverview(Long doctorId) {
        List<Consultation> consultations = consultationRepository.findByDoctor_IdOrderByDateTimeDesc(doctorId);
        List<Prescription> prescriptions = prescriptionRepository.findByConsultation_Doctor_IdOrderByIdDesc(doctorId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("todaysAppointments", appointmentRepository.findByDoctor_IdAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            doctorId,
            LocalDate.now().atStartOfDay(),
            LocalDate.now().plusDays(1).atStartOfDay()
        ));
        response.put("totalPatientsTreated", consultations.stream().map(c -> c.getPatient().getId()).collect(Collectors.toSet()).size());
        response.put("recentPrescriptions", prescriptions.stream().limit(5).toList());
        return response;
    }

    public Map<String, Object> adminOverview() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalHospitals", hospitalRepository.count());
        response.put("totalDoctors", doctorRepository.count());
        response.put("totalPatients", patientRepository.count());
        response.put("totalCorruptionAlerts", alertRepository.findAll().size());
        return response;
    }

    public Map<String, Object> analytics() {
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        List<Alert> alerts = alertRepository.findAll();
        List<Consultation> consultations = consultationRepository.findAll();

        Map<String, Long> mostPrescribedMedicines = prescriptions.stream()
            .flatMap(p -> p.getMedicines().stream())
            .collect(Collectors.groupingBy(Medicine::getName, Collectors.counting()));

        Map<String, BigDecimal> doctorCostMap = prescriptions.stream()
            .filter(p -> p.getConsultation() != null && p.getConsultation().getDoctor() != null)
            .collect(Collectors.groupingBy(
                p -> p.getConsultation().getDoctor().getName(),
                Collectors.mapping(Prescription::getTotalCost, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        Map<String, Long> hospitalAlertMap = alerts.stream()
            .filter(a -> a.getDoctor() != null && a.getDoctor().getHospital() != null)
            .collect(Collectors.groupingBy(a -> a.getDoctor().getHospital().getName(), Collectors.counting()));

        Map<String, Long> monthlyVisits = consultations.stream()
            .collect(Collectors.groupingBy(c -> c.getDateTime().getYear() + "-" + String.format("%02d", c.getDateTime().getMonthValue()), Collectors.counting()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mostPrescribedMedicines", mostPrescribedMedicines);
        response.put("doctorsWithHighestPrescriptionCost", doctorCostMap);
        response.put("hospitalsWithMostAlerts", hospitalAlertMap);
        response.put("monthlyPatientVisits", monthlyVisits);
        return response;
    }
}
