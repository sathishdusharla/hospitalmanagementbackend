package com.example.backend;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private DoctorRepository     doctorRepository;
    @Autowired private StaffRepository      staffRepository;
    @Autowired private PatientRepository    patientRepository;
    @Autowired private DiseaseRepository    diseaseRepository;
    @Autowired private MedicineRepository   medicineRepository;
    @Autowired private AppUserRepository    appUserRepository;
    @Autowired private PasswordEncoder      passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // ── Hospitals ────────────────────────────────────────────────────────────
        Map<String, Hospital> byName = hospitalRepository.findAll().stream()
            .collect(Collectors.toMap(Hospital::getName, h -> h, (a, b) -> a, HashMap::new));

        Hospital cityGeneral = ensureHospital(byName, "City General Hospital",           "123 Main St, City",    "123-456-7890");
        Hospital sunrise     = ensureHospital(byName, "Sunrise Multispeciality Hospital","45 Lake View Road",    "123-456-7891");
        Hospital greenValley = ensureHospital(byName, "Green Valley Medical Center",     "78 River Park Avenue", "123-456-7892");
        Hospital metroCare   = ensureHospital(byName, "MetroCare Institute",             "12 Central Square",    "123-456-7893");
        Hospital hopeWell    = ensureHospital(byName, "HopeWell Community Hospital",     "9 Garden Street",      "123-456-7894");
        Hospital medTrust    = ensureHospital(byName, "MedTrust",                         "Hyderabad, Telangana", "+91-9876543210");

        // ── Doctors ───────────────────────────────────────────────────────────────
        Map<String, Doctor> byLicense = doctorRepository.findAll().stream()
            .collect(Collectors.toMap(Doctor::getLicenseNumber, d -> d, (a, b) -> a, HashMap::new));

        Doctor drJohnDoe      = ensureDoctor(byLicense, "Dr. John Doe",       "General Medicine", "LIC12345", cityGeneral);
                                ensureDoctor(byLicense, "Dr. Meera Nair",     "Cardiology",       "LIC20001", sunrise);
                                ensureDoctor(byLicense, "Dr. Arjun Verma",    "Neurology",        "LIC20002", greenValley);
                                ensureDoctor(byLicense, "Dr. Priya Shah",     "Orthopedics",      "LIC20003", metroCare);
                                ensureDoctor(byLicense, "Dr. Rohan Kulkarni", "Dermatology",      "LIC20004", hopeWell);
                                ensureDoctor(byLicense, "Dr. Sana Ali",       "Pediatrics",       "LIC20005", cityGeneral);
                                ensureDoctor(byLicense, "Dr. Vikram Iyer",    "ENT",              "LIC20006", sunrise);
                                ensureDoctor(byLicense, "Dr. Ananya Rao",     "Gynecology",       "LIC20007", greenValley);
                                ensureDoctor(byLicense, "Dr. Karan Gupta",    "Pulmonology",      "LIC20008", metroCare);
                                ensureDoctor(byLicense, "Dr. Neha Bansal",    "Psychiatry",       "LIC20009", hopeWell);
        Doctor drSathish      = ensureDoctor(byLicense, "Dr. Sathish Dusharla", "General Physician", "MED-2024-SD-001", medTrust);

        // ── Staff ─────────────────────────────────────────────────────────────────
        if (staffRepository.count() == 0) {
            Staff staff = new Staff();
            staff.setName("Jane Smith");
            staff.setRole("Nurse");
            staff.setEmployeeId("EMP001");
            staff.setHospital(cityGeneral);
            staffRepository.save(staff);
        }

        // ── Patients ──────────────────────────────────────────────────────────────
        Patient aliceJohnson;
        Patient raghu;
        if (patientRepository.count() == 0) {
            List<Patient> saved = patientRepository.saveAll(List.of(
                buildPatient("Alice Johnson", "1990-01-01", "Female", "alice@example.com",   cityGeneral),
                buildPatient("Rahul Mehta",   "1988-06-11", "Male",   "rahul@example.com",   sunrise),
                buildPatient("Divya Menon",   "1995-09-18", "Female", "divya@example.com",   greenValley),
                buildPatient("Karthik Raj",   "1992-03-22", "Male",   "karthik@example.com", metroCare),
                buildPatient("Raghu",         "1995-05-15", "Male",   "raghu@example.com",   medTrust)
            ));
            aliceJohnson = saved.get(0);
            raghu = saved.get(4);
        } else {
            aliceJohnson = patientRepository.findAll().get(0);
            raghu = patientRepository.findAll().stream()
                .filter(p -> "Raghu".equals(p.getName()))
                .findFirst()
                .orElse(aliceJohnson);
        }

        // ── Diseases + Medicines ──────────────────────────────────────────────────
        Disease disease = diseaseRepository.findByNameIgnoreCase("Common Cold")
            .orElseGet(() -> {
                Disease d = new Disease();
                d.setName("Common Cold");
                d.setDescription("Viral infection of the upper respiratory tract");
                return diseaseRepository.save(d);
            });

        Map<String, Medicine> medByName = medicineRepository.findAll().stream()
            .collect(Collectors.toMap(Medicine::getName, m -> m, (a, b) -> a, HashMap::new));

        Medicine med1 = medByName.computeIfAbsent("Paracetamol", k -> {
            Medicine m = new Medicine();
            m.setName("Paracetamol");
            m.setDescription("Pain reliever and fever reducer");
            m.setCost(new BigDecimal("25.00"));
            m.setStandardPrice(new BigDecimal("30.00"));
            m.setExpensive(false);
            return medicineRepository.save(m);
        });

        Medicine med2 = medByName.computeIfAbsent("Expensive Drug", k -> {
            Medicine m = new Medicine();
            m.setName("Expensive Drug");
            m.setDescription("Very expensive medicine");
            m.setCost(new BigDecimal("1000.00"));
            m.setStandardPrice(new BigDecimal("800.00"));
            m.setExpensive(true);
            return medicineRepository.save(m);
        });

        // Additional common medicines
        medByName.computeIfAbsent("Amoxicillin", k -> createMedicine("Amoxicillin", "Antibiotic for bacterial infections", "80.00", "100.00", false));
        medByName.computeIfAbsent("Ibuprofen", k -> createMedicine("Ibuprofen", "Anti-inflammatory pain reliever", "35.00", "40.00", false));
        medByName.computeIfAbsent("Omeprazole", k -> createMedicine("Omeprazole", "Reduces stomach acid", "45.00", "55.00", false));
        medByName.computeIfAbsent("Metformin", k -> createMedicine("Metformin", "Diabetes medication", "60.00", "75.00", false));
        medByName.computeIfAbsent("Azithromycin", k -> createMedicine("Azithromycin", "Antibiotic (Z-Pack)", "150.00", "180.00", false));
        medByName.computeIfAbsent("Cetirizine", k -> createMedicine("Cetirizine", "Antihistamine for allergies", "20.00", "25.00", false));
        medByName.computeIfAbsent("Dolo 650", k -> createMedicine("Dolo 650", "Fever and pain relief", "30.00", "35.00", false));
        medByName.computeIfAbsent("Crocin", k -> createMedicine("Crocin", "Paracetamol brand for fever", "28.00", "32.00", false));
        medByName.computeIfAbsent("Vitamin D3", k -> createMedicine("Vitamin D3", "Vitamin supplement", "200.00", "250.00", true));
        medByName.computeIfAbsent("B-Complex", k -> createMedicine("B-Complex", "Vitamin B complex supplement", "90.00", "110.00", false));
        medByName.computeIfAbsent("Montelukast", k -> createMedicine("Montelukast", "Asthma and allergy medication", "180.00", "220.00", true));
        medByName.computeIfAbsent("Atorvastatin", k -> createMedicine("Atorvastatin", "Cholesterol lowering medication", "250.00", "300.00", true));

        // new ArrayList<> is required — Arrays.asList returns a fixed-size list JPA cannot modify
        disease.setRecommendedMedicines(new ArrayList<>(Arrays.asList(med1, med2)));
        diseaseRepository.save(disease);

        // ── Demo AppUsers ─────────────────────────────────────────────────────────
        seedDemoUser("admin",   "ADMIN",    "System Administrator", null,                  null, "password");
        seedDemoUser("doctor",  "DOCTOR",   drJohnDoe.getName(),    drJohnDoe.getId(),     null, "password");
        seedDemoUser("patient", "PATIENT",  aliceJohnson.getName(), null,                  aliceJohnson.getId(), "password");
        // Custom demo users
        seedDemoUser("ram",      "ADMIN",   "Ram",                  null,                  null, "ram123");
        seedDemoUser("drsathish","DOCTOR",  "Dr. Sathish Dusharla", drSathish.getId(),     null, "doctor123");
        seedDemoUser("raghu",    "PATIENT", "Raghu",                null,                  raghu.getId(), "raghu123");

        log.info("Seed complete — hospitals: {}, doctors: {}, patients: {}",
            hospitalRepository.count(), doctorRepository.count(), patientRepository.count());
    }

    private void seedDemoUser(String username, String role, String fullName, Long doctorId, Long patientId, String password) {
        if (appUserRepository.findByUsernameIgnoreCase(username).isPresent()) return;
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        u.setFullName(fullName);
        if (doctorId != null)  u.setLinkedEntityId(doctorId);
        if (patientId != null) u.setLinkedEntityId(patientId);
        appUserRepository.save(u);
    }

    private Medicine createMedicine(String name, String description, String cost, String standardPrice, boolean expensive) {
        Medicine m = new Medicine();
        m.setName(name);
        m.setDescription(description);
        m.setCost(new BigDecimal(cost));
        m.setStandardPrice(new BigDecimal(standardPrice));
        m.setExpensive(expensive);
        return medicineRepository.save(m);
    }

    private Hospital ensureHospital(Map<String, Hospital> map, String name, String address, String phone) {
        Hospital h = map.get(name);
        if (h != null) return h;
        Hospital hospital = new Hospital();
        hospital.setName(name);
        hospital.setAddress(address);
        hospital.setPhone(phone);
        hospital.setApproved(true);
        Hospital saved = hospitalRepository.save(hospital);
        map.put(name, saved);
        return saved;
    }

    private Doctor ensureDoctor(Map<String, Doctor> map, String name, String spec, String license, Hospital hospital) {
        Doctor d = map.get(license);
        if (d != null) return d;
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSpecialization(spec);
        doctor.setLicenseNumber(license);
        doctor.setHospital(hospital);
        doctor.setApproved(true);
        Doctor saved = doctorRepository.save(doctor);
        map.put(license, saved);
        return saved;
    }

    private Patient buildPatient(String name, String dob, String gender, String contact, Hospital hospital) {
        Patient p = new Patient();
        p.setName(name);
        p.setDateOfBirth(dob);
        p.setGender(gender);
        p.setContactInfo(contact);
        p.setHospital(hospital);
        return p;
    }
}
