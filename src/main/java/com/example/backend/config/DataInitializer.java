package com.example.backend.config;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Disabled - using DataLoader instead
// @Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (hospitalRepository.count() > 0) {
            System.out.println("✅ Database already contains data. Skipping initialization.");
            return;
        }

        System.out.println("🚀 Initializing dummy data...");

        // 1. Create Hospital "MedTrust"
        Hospital hospital = new Hospital();
        hospital.setName("MedTrust");
        hospital.setAddress("Hyderabad, Telangana");
        hospital.setPhone("+91-9876543210");
        hospital.setApproved(true);
        hospital = hospitalRepository.save(hospital);
        System.out.println("✅ Hospital created: MedTrust");

        // 2. Create Admin/Reception "Ram"
        AppUser adminUser = new AppUser();
        adminUser.setUsername("ram");
        adminUser.setPassword(passwordEncoder.encode("ram123"));
        adminUser.setRole("ADMIN");
        adminUser.setFullName("Ram");
        adminUser.setLinkedEntityId(null);
        appUserRepository.save(adminUser);
        System.out.println("✅ Admin created: Ram (username: ram, password: ram123)");

        // 3. Create Doctor "Dr. Sathish Dusharla"
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Sathish Dusharla");
        doctor.setSpecialization("General Physician");
        doctor.setLicenseNumber("MED-2024-SD-001");
        doctor.setHospital(hospital);
        doctor.setApproved(true);
        doctor = doctorRepository.save(doctor);
        System.out.println("✅ Doctor created: Dr. Sathish Dusharla");

        AppUser doctorUser = new AppUser();
        doctorUser.setUsername("drsathish");
        doctorUser.setPassword(passwordEncoder.encode("doctor123"));
        doctorUser.setRole("DOCTOR");
        doctorUser.setFullName("Dr. Sathish Dusharla");
        doctorUser.setLinkedEntityId(doctor.getId());
        appUserRepository.save(doctorUser);
        System.out.println("✅ Doctor login created (username: drsathish, password: doctor123)");

        // 4. Create Patient "Raghu"
        Patient patient = new Patient();
        patient.setName("Raghu");
        patient.setDateOfBirth("1995-05-15");
        patient.setGender("Male");
        patient.setContactInfo("Phone: +91-8899001122, Email: raghu@example.com");
        patient.setHospital(hospital);
        patient = patientRepository.save(patient);
        System.out.println("✅ Patient created: Raghu");

        AppUser patientUser = new AppUser();
        patientUser.setUsername("raghu");
        patientUser.setPassword(passwordEncoder.encode("raghu123"));
        patientUser.setRole("PATIENT");
        patientUser.setFullName("Raghu");
        patientUser.setLinkedEntityId(patient.getId());
        appUserRepository.save(patientUser);
        System.out.println("✅ Patient login created (username: raghu, password: raghu123)");

        // 5. Create Appointment for Raghu with Dr. Sathish Dusharla
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setHospital(hospital);
        appointment.setAppointmentDateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0));
        appointment.setDayPatientNumber(1);
        appointment.setSymptomsOrDisease("General checkup and fever");
        appointment.setStatus("SCHEDULED");
        appointmentRepository.save(appointment);
        System.out.println("✅ Appointment created: Raghu → Dr. Sathish Dusharla (Tomorrow 10:00 AM)");

        // 6. Create Medicines
        String[][] medicineData = {
            {"Paracetamol", "Pain reliever and fever reducer", "25", "30", "false"},
            {"Amoxicillin", "Antibiotic for bacterial infections", "80", "100", "false"},
            {"Ibuprofen", "Anti-inflammatory pain reliever", "35", "40", "false"},
            {"Omeprazole", "Reduces stomach acid", "45", "55", "false"},
            {"Metformin", "Diabetes medication", "60", "75", "false"},
            {"Azithromycin", "Antibiotic (Z-Pack)", "150", "180", "false"},
            {"Cetirizine", "Antihistamine for allergies", "20", "25", "false"},
            {"Pantoprazole", "Gastric acid reducer", "55", "70", "false"},
            {"Ciprofloxacin", "Broad-spectrum antibiotic", "120", "150", "false"},
            {"Dolo 650", "Fever and pain relief", "30", "35", "false"},
            {"Crocin", "Paracetamol brand for fever", "28", "32", "false"},
            {"Vitamin D3", "Vitamin supplement", "200", "250", "true"},
            {"B-Complex", "Vitamin B complex supplement", "90", "110", "false"},
            {"Montelukast", "Asthma and allergy medication", "180", "220", "true"},
            {"Atorvastatin", "Cholesterol lowering medication", "250", "300", "true"}
        };

        for (String[] med : medicineData) {
            Medicine medicine = new Medicine();
            medicine.setName(med[0]);
            medicine.setDescription(med[1]);
            medicine.setCost(new BigDecimal(med[2]));
            medicine.setStandardPrice(new BigDecimal(med[3]));
            medicine.setExpensive(Boolean.parseBoolean(med[4]));
            medicineRepository.save(medicine);
        }
        System.out.println("✅ Medicines created: " + medicineData.length + " medicines added");

        System.out.println("\n🎉 Dummy data initialization complete!");
        System.out.println("\n📋 Demo Credentials:");
        System.out.println("   Admin: ram / ram123");
        System.out.println("   Doctor: drsathish / doctor123");
        System.out.println("   Patient: raghu / raghu123");
    }
}
