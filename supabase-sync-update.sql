-- ============================================
-- MediTrust Supabase Synchronization Update SQL
-- Run this in Supabase SQL Editor to update your database
-- ============================================

-- ============================================
-- STEP 1: DROP AND RECREATE TABLES
-- ============================================
DROP TABLE IF EXISTS public.corruption_alert CASCADE;
DROP TABLE IF EXISTS public.prescription_medicine CASCADE;
DROP TABLE IF EXISTS public.prescription CASCADE;
DROP TABLE IF EXISTS public.lab_test CASCADE;
DROP TABLE IF EXISTS public.consultation CASCADE;
DROP TABLE IF EXISTS public.appointment CASCADE;
DROP TABLE IF EXISTS public.disease_medicine CASCADE;
DROP TABLE IF EXISTS public.medicine CASCADE;
DROP TABLE IF EXISTS public.disease CASCADE;
DROP TABLE IF EXISTS public.patient CASCADE;
DROP TABLE IF EXISTS public.staff CASCADE;
DROP TABLE IF EXISTS public.doctor CASCADE;
DROP TABLE IF EXISTS public.app_user CASCADE;
DROP TABLE IF EXISTS public.hospital CASCADE;

-- ============================================
-- STEP 2: CREATE TABLES WITH SYNC-FRIENDLY STRUCTURE
-- ============================================

-- Hospital Table
CREATE TABLE public.hospital (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(50),
    approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Doctor Table (synced with appointments)
CREATE TABLE public.doctor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255),
    license_number VARCHAR(100) UNIQUE,
    hospital_id BIGINT REFERENCES public.hospital(id) ON DELETE SET NULL,
    approved BOOLEAN DEFAULT TRUE,
    email VARCHAR(255),
    phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Staff Table
CREATE TABLE public.staff (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    employee_id VARCHAR(100) UNIQUE,
    hospital_id BIGINT REFERENCES public.hospital(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Patient Table (synced with email mapping)
CREATE TABLE public.patient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_of_birth VARCHAR(50),
    gender VARCHAR(20),
    contact_info VARCHAR(500),  -- Stores email and phone: "email@example.com, +1234567890"
    hospital_id BIGINT REFERENCES public.hospital(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- App User Table (Authentication - linked to patient/doctor)
CREATE TABLE public.app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,  -- ADMIN, DOCTOR, PATIENT
    full_name VARCHAR(255),
    linked_entity_id BIGINT,  -- Links to doctor.id or patient.id based on role
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Disease Table
CREATE TABLE public.disease (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Medicine Table
CREATE TABLE public.medicine (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cost DECIMAL(10,2),
    standard_price DECIMAL(10,2),
    is_expensive BOOLEAN DEFAULT FALSE
);

-- Disease-Medicine Junction Table
CREATE TABLE public.disease_medicine (
    disease_id BIGINT REFERENCES public.disease(id) ON DELETE CASCADE,
    medicine_id BIGINT REFERENCES public.medicine(id) ON DELETE CASCADE,
    PRIMARY KEY (disease_id, medicine_id)
);

-- Consultation Table (synced between doctor and patient)
CREATE TABLE public.consultation (
    id BIGSERIAL PRIMARY KEY,
    consultation_date TIMESTAMP,
    symptoms TEXT,
    diagnosis TEXT,
    notes TEXT,
    consultation_fee DECIMAL(10,2),
    standard_fee DECIMAL(10,2),
    doctor_id BIGINT REFERENCES public.doctor(id) ON DELETE SET NULL,
    patient_id BIGINT REFERENCES public.patient(id) ON DELETE CASCADE,
    disease_id BIGINT REFERENCES public.disease(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Appointment Table (fully synced across admin, doctor, patient)
CREATE TABLE public.appointment (
    id BIGSERIAL PRIMARY KEY,
    appointment_date_time TIMESTAMP NOT NULL,
    symptoms_or_disease TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',  -- PENDING, CONFIRMED, COMPLETED, CANCELLED
    consultation_fee DECIMAL(10,2),
    day_patient_number INTEGER,
    hospital_id BIGINT REFERENCES public.hospital(id) ON DELETE SET NULL,
    doctor_id BIGINT REFERENCES public.doctor(id) ON DELETE SET NULL,
    patient_id BIGINT REFERENCES public.patient(id) ON DELETE CASCADE,
    consultation_id BIGINT REFERENCES public.consultation(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Lab Test Table (synced with consultation)
CREATE TABLE public.lab_test (
    id BIGSERIAL PRIMARY KEY,
    test_name VARCHAR(255),
    instructions TEXT,
    result TEXT,
    file_path VARCHAR(500),
    status VARCHAR(50) DEFAULT 'PENDING',  -- PENDING, COMPLETED
    ordered_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP,
    consultation_id BIGINT REFERENCES public.consultation(id) ON DELETE CASCADE,
    patient_id BIGINT REFERENCES public.patient(id) ON DELETE CASCADE,
    doctor_id BIGINT REFERENCES public.doctor(id) ON DELETE SET NULL
);

-- Prescription Table
CREATE TABLE public.prescription (
    id BIGSERIAL PRIMARY KEY,
    notes TEXT,
    total_cost DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT NOW(),
    consultation_id BIGINT REFERENCES public.consultation(id) ON DELETE CASCADE
);

-- Prescription-Medicine Junction Table
CREATE TABLE public.prescription_medicine (
    prescription_id BIGINT REFERENCES public.prescription(id) ON DELETE CASCADE,
    medicine_id BIGINT REFERENCES public.medicine(id) ON DELETE CASCADE,
    PRIMARY KEY (prescription_id, medicine_id)
);

-- Corruption Alert Table
CREATE TABLE public.corruption_alert (
    id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(100),
    description TEXT,
    severity VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    reviewed BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    prescription_id BIGINT REFERENCES public.prescription(id) ON DELETE CASCADE,
    doctor_id BIGINT REFERENCES public.doctor(id) ON DELETE SET NULL,
    patient_id BIGINT REFERENCES public.patient(id) ON DELETE CASCADE
);

-- ============================================
-- STEP 3: CREATE INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX idx_doctor_hospital ON public.doctor(hospital_id);
CREATE INDEX idx_doctor_email ON public.doctor(email);
CREATE INDEX idx_patient_hospital ON public.patient(hospital_id);
CREATE INDEX idx_patient_contact ON public.patient(contact_info);
CREATE INDEX idx_appointment_doctor ON public.appointment(doctor_id);
CREATE INDEX idx_appointment_patient ON public.appointment(patient_id);
CREATE INDEX idx_appointment_status ON public.appointment(status);
CREATE INDEX idx_appointment_datetime ON public.appointment(appointment_date_time);
CREATE INDEX idx_consultation_doctor ON public.consultation(doctor_id);
CREATE INDEX idx_consultation_patient ON public.consultation(patient_id);
CREATE INDEX idx_app_user_username ON public.app_user(username);
CREATE INDEX idx_app_user_role ON public.app_user(role);
CREATE INDEX idx_app_user_linked ON public.app_user(linked_entity_id);
CREATE INDEX idx_lab_test_consultation ON public.lab_test(consultation_id);
CREATE INDEX idx_lab_test_patient ON public.lab_test(patient_id);

-- ============================================
-- STEP 4: INSERT SYNCHRONIZED DEMO DATA
-- ============================================

-- Insert Hospitals
INSERT INTO public.hospital (name, address, phone, approved) VALUES
('City General Hospital', '123 Main St, City', '123-456-7890', TRUE),
('Sunrise Multispeciality Hospital', '45 Lake View Road', '123-456-7891', TRUE),
('Green Valley Medical Center', '78 River Park Avenue', '123-456-7892', TRUE),
('MetroCare Institute', '12 Central Square', '123-456-7893', TRUE),
('HopeWell Community Hospital', '9 Garden Street', '123-456-7894', TRUE),
('MedTrust', 'Hyderabad, Telangana', '+91-9876543210', TRUE);

-- Insert Doctors (with email for mapping)
INSERT INTO public.doctor (name, specialization, license_number, hospital_id, approved, email) VALUES
('Dr. John Doe', 'General Medicine', 'LIC12345', 1, TRUE, 'johndoe@hospital.com'),
('Dr. Meera Nair', 'Cardiology', 'LIC20001', 2, TRUE, 'meera@hospital.com'),
('Dr. Arjun Verma', 'Neurology', 'LIC20002', 3, TRUE, 'arjun@hospital.com'),
('Dr. Priya Shah', 'Orthopedics', 'LIC20003', 4, TRUE, 'priya@hospital.com'),
('Dr. Rohan Kulkarni', 'Dermatology', 'LIC20004', 5, TRUE, 'rohan@hospital.com'),
('Dr. Sana Ali', 'Pediatrics', 'LIC20005', 1, TRUE, 'sana@hospital.com'),
('Dr. Vikram Iyer', 'ENT', 'LIC20006', 2, TRUE, 'vikram@hospital.com'),
('Dr. Ananya Rao', 'Gynecology', 'LIC20007', 3, TRUE, 'ananya@hospital.com'),
('Dr. Karan Gupta', 'Pulmonology', 'LIC20008', 4, TRUE, 'karan@hospital.com'),
('Dr. Neha Bansal', 'Psychiatry', 'LIC20009', 5, TRUE, 'neha@hospital.com'),
('Dr. Sathish Dusharla', 'General Physician', 'MED-2024-SD-001', 6, TRUE, 'drsathish@meditrust.com');

-- Insert Staff
INSERT INTO public.staff (name, role, employee_id, hospital_id) VALUES
('Jane Smith', 'Nurse', 'EMP001', 1);

-- Insert Patients (with email in contact_info for mapping)
INSERT INTO public.patient (name, date_of_birth, gender, contact_info, hospital_id) VALUES
('Alice Johnson', '1990-01-01', 'Female', 'alice@example.com, +1-555-0101', 1),
('Rahul Mehta', '1988-06-11', 'Male', 'rahul@example.com, +91-9876543201', 2),
('Divya Menon', '1995-09-18', 'Female', 'divya@example.com, +91-9876543202', 3),
('Karthik Raj', '1992-03-22', 'Male', 'karthik@example.com, +91-9876543203', 4),
('Raghu', '1995-05-15', 'Male', 'raghu@example.com, +91-9876543210', 6);

-- Insert App Users (BCrypt hashed passwords)
-- Default password: "password" | Custom: ram123, doctor123, raghu123
INSERT INTO public.app_user (username, password, role, full_name, linked_entity_id) VALUES
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', 'System Administrator', NULL),
('doctor', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'DOCTOR', 'Dr. John Doe', 1),
('patient', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'PATIENT', 'Alice Johnson', 1),
('ram', '$2a$10$N9qo8uLOickgx2ZMRZoHK.TqyVG0Zx6gZjO3M9XKc1cKqY2q1JVVW', 'ADMIN', 'Ram', NULL),
('drsathish', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', 'DOCTOR', 'Dr. Sathish Dusharla', 11),
('raghu', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'PATIENT', 'Raghu', 5);

-- Insert Diseases
INSERT INTO public.disease (name, description) VALUES
('Common Cold', 'Viral infection of the upper respiratory tract'),
('Fever', 'Elevated body temperature due to infection'),
('Diabetes', 'Metabolic disease causing high blood sugar'),
('Hypertension', 'High blood pressure condition'),
('Allergies', 'Immune system reaction to substances');

-- Insert Medicines
INSERT INTO public.medicine (name, description, cost, standard_price, is_expensive) VALUES
('Paracetamol', 'Pain reliever and fever reducer', 25.00, 30.00, FALSE),
('Expensive Drug', 'Very expensive medicine', 1000.00, 800.00, TRUE),
('Amoxicillin', 'Antibiotic for bacterial infections', 80.00, 100.00, FALSE),
('Ibuprofen', 'Anti-inflammatory pain reliever', 35.00, 40.00, FALSE),
('Omeprazole', 'Reduces stomach acid', 45.00, 55.00, FALSE),
('Metformin', 'Diabetes medication', 60.00, 75.00, FALSE),
('Azithromycin', 'Antibiotic (Z-Pack)', 150.00, 180.00, FALSE),
('Cetirizine', 'Antihistamine for allergies', 20.00, 25.00, FALSE),
('Dolo 650', 'Fever and pain relief', 30.00, 35.00, FALSE),
('Crocin', 'Paracetamol brand for fever', 28.00, 32.00, FALSE),
('Vitamin D3', 'Vitamin supplement', 200.00, 250.00, TRUE),
('B-Complex', 'Vitamin B complex supplement', 90.00, 110.00, FALSE),
('Montelukast', 'Asthma and allergy medication', 180.00, 220.00, TRUE),
('Atorvastatin', 'Cholesterol lowering medication', 250.00, 300.00, TRUE);

-- Link Diseases to Medicines
INSERT INTO public.disease_medicine (disease_id, medicine_id) VALUES
(1, 1), (1, 2), (2, 1), (2, 9), (2, 10),
(3, 6), (4, 14), (5, 8), (5, 13);

-- Insert Sample Synced Appointment (Raghu with Dr. Sathish at MedTrust)
INSERT INTO public.appointment (
    appointment_date_time, symptoms_or_disease, status,
    consultation_fee, day_patient_number, hospital_id, doctor_id, patient_id
) VALUES (
    NOW() + INTERVAL '1 day', 'General checkup and fever', 'PENDING',
    200.00, 1, 6, 11, 5
);

-- ============================================
-- STEP 5: CREATE VIEW FOR SYNCHRONIZED DATA
-- ============================================

-- View: Complete appointment details for synchronization
CREATE OR REPLACE VIEW public.v_appointment_details AS
SELECT
    a.id AS appointment_id,
    a.appointment_date_time,
    a.status,
    a.symptoms_or_disease,
    a.consultation_fee,
    a.day_patient_number,
    p.id AS patient_id,
    p.name AS patient_name,
    p.contact_info AS patient_contact,
    d.id AS doctor_id,
    d.name AS doctor_name,
    d.specialization AS doctor_specialization,
    h.id AS hospital_id,
    h.name AS hospital_name,
    c.id AS consultation_id,
    c.diagnosis,
    a.created_at,
    a.updated_at
FROM public.appointment a
LEFT JOIN public.patient p ON a.patient_id = p.id
LEFT JOIN public.doctor d ON a.doctor_id = d.id
LEFT JOIN public.hospital h ON a.hospital_id = h.id
LEFT JOIN public.consultation c ON a.consultation_id = c.id
ORDER BY a.appointment_date_time DESC;

-- View: Patient details with linked user account
CREATE OR REPLACE VIEW public.v_patient_with_user AS
SELECT
    p.id AS patient_id,
    p.name,
    p.date_of_birth,
    p.gender,
    p.contact_info,
    h.name AS hospital_name,
    u.username,
    u.id AS user_id
FROM public.patient p
LEFT JOIN public.hospital h ON p.hospital_id = h.id
LEFT JOIN public.app_user u ON u.linked_entity_id = p.id AND u.role = 'PATIENT';

-- ============================================
-- STEP 6: VERIFY DATA
-- ============================================
SELECT 'Hospitals: ' || COUNT(*)::text FROM public.hospital
UNION ALL SELECT 'Doctors: ' || COUNT(*)::text FROM public.doctor
UNION ALL SELECT 'Patients: ' || COUNT(*)::text FROM public.patient
UNION ALL SELECT 'Users: ' || COUNT(*)::text FROM public.app_user
UNION ALL SELECT 'Medicines: ' || COUNT(*)::text FROM public.medicine
UNION ALL SELECT 'Appointments: ' || COUNT(*)::text FROM public.appointment;
