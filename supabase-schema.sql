-- ============================================
-- MediTrust Supabase PostgreSQL Schema
-- Run this in Supabase SQL Editor
-- ============================================

-- Drop existing tables (in reverse order of dependencies)
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
-- 1. Hospital Table
-- ============================================
CREATE TABLE hospital (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(50),
    approved BOOLEAN DEFAULT TRUE
);

-- ============================================
-- 2. Doctor Table
-- ============================================
CREATE TABLE doctor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255),
    license_number VARCHAR(100) UNIQUE,
    hospital_id BIGINT REFERENCES hospital(id),
    approved BOOLEAN DEFAULT TRUE
);

-- ============================================
-- 3. Staff Table
-- ============================================
CREATE TABLE staff (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    employee_id VARCHAR(100) UNIQUE,
    hospital_id BIGINT REFERENCES hospital(id)
);

-- ============================================
-- 4. Patient Table
-- ============================================
CREATE TABLE patient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_of_birth VARCHAR(50),
    gender VARCHAR(20),
    contact_info VARCHAR(500),
    hospital_id BIGINT REFERENCES hospital(id)
);

-- ============================================
-- 5. App User Table (Authentication)
-- ============================================
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    full_name VARCHAR(255),
    linked_entity_id BIGINT
);

-- ============================================
-- 6. Disease Table
-- ============================================
CREATE TABLE disease (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- ============================================
-- 7. Medicine Table
-- ============================================
CREATE TABLE medicine (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cost DECIMAL(10,2),
    standard_price DECIMAL(10,2),
    is_expensive BOOLEAN DEFAULT FALSE
);

-- ============================================
-- 8. Disease-Medicine Junction Table
-- ============================================
CREATE TABLE disease_medicine (
    disease_id BIGINT REFERENCES disease(id),
    medicine_id BIGINT REFERENCES medicine(id),
    PRIMARY KEY (disease_id, medicine_id)
);

-- ============================================
-- 9. Consultation Table
-- ============================================
CREATE TABLE consultation (
    id BIGSERIAL PRIMARY KEY,
    consultation_date TIMESTAMP,
    diagnosis TEXT,
    notes TEXT,
    doctor_id BIGINT REFERENCES doctor(id),
    patient_id BIGINT REFERENCES patient(id),
    disease_id BIGINT REFERENCES disease(id)
);

-- ============================================
-- 10. Appointment Table
-- ============================================
CREATE TABLE appointment (
    id BIGSERIAL PRIMARY KEY,
    appointment_date_time TIMESTAMP,
    symptoms_or_disease TEXT,
    status VARCHAR(50),
    consultation_fee DECIMAL(10,2),
    day_patient_number INTEGER,
    hospital_id BIGINT REFERENCES hospital(id),
    doctor_id BIGINT REFERENCES doctor(id),
    patient_id BIGINT REFERENCES patient(id),
    consultation_id BIGINT REFERENCES consultation(id)
);

-- ============================================
-- 11. Lab Test Table
-- ============================================
CREATE TABLE lab_test (
    id BIGSERIAL PRIMARY KEY,
    test_name VARCHAR(255),
    instructions TEXT,
    result TEXT,
    file_path VARCHAR(500),
    status VARCHAR(50),
    ordered_at TIMESTAMP,
    completed_at TIMESTAMP,
    consultation_id BIGINT REFERENCES consultation(id),
    patient_id BIGINT REFERENCES patient(id),
    doctor_id BIGINT REFERENCES doctor(id)
);

-- ============================================
-- 12. Prescription Table
-- ============================================
CREATE TABLE prescription (
    id BIGSERIAL PRIMARY KEY,
    notes TEXT,
    total_cost DECIMAL(10,2),
    created_at TIMESTAMP,
    consultation_id BIGINT REFERENCES consultation(id)
);

-- ============================================
-- 13. Prescription-Medicine Junction Table
-- ============================================
CREATE TABLE prescription_medicine (
    prescription_id BIGINT REFERENCES prescription(id),
    medicine_id BIGINT REFERENCES medicine(id),
    PRIMARY KEY (prescription_id, medicine_id)
);

-- ============================================
-- 14. Corruption Alert Table
-- ============================================
CREATE TABLE corruption_alert (
    id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(100),
    description TEXT,
    severity VARCHAR(50),
    created_at TIMESTAMP,
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    prescription_id BIGINT REFERENCES prescription(id),
    doctor_id BIGINT REFERENCES doctor(id),
    patient_id BIGINT REFERENCES patient(id)
);

-- ============================================
-- Create Indexes for Better Performance
-- ============================================
CREATE INDEX idx_doctor_hospital ON doctor(hospital_id);
CREATE INDEX idx_patient_hospital ON patient(hospital_id);
CREATE INDEX idx_appointment_doctor ON appointment(doctor_id);
CREATE INDEX idx_appointment_patient ON appointment(patient_id);
CREATE INDEX idx_consultation_doctor ON consultation(doctor_id);
CREATE INDEX idx_consultation_patient ON consultation(patient_id);
CREATE INDEX idx_app_user_username ON app_user(username);
CREATE INDEX idx_lab_test_consultation ON lab_test(consultation_id);
