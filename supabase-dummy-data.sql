-- ============================================
-- MediTrust Supabase Dummy Data
-- Run this AFTER the schema SQL
-- ============================================

-- ============================================
-- 1. Insert Hospitals
-- ============================================
INSERT INTO hospital (name, address, phone, approved) VALUES
('City General Hospital', '123 Main St, City', '123-456-7890', TRUE),
('Sunrise Multispeciality Hospital', '45 Lake View Road', '123-456-7891', TRUE),
('Green Valley Medical Center', '78 River Park Avenue', '123-456-7892', TRUE),
('MetroCare Institute', '12 Central Square', '123-456-7893', TRUE),
('HopeWell Community Hospital', '9 Garden Street', '123-456-7894', TRUE),
('MedTrust', 'Hyderabad, Telangana', '+91-9876543210', TRUE);

-- ============================================
-- 2. Insert Doctors
-- ============================================
INSERT INTO doctor (name, specialization, license_number, hospital_id, approved) VALUES
('Dr. John Doe', 'General Medicine', 'LIC12345', 1, TRUE),
('Dr. Meera Nair', 'Cardiology', 'LIC20001', 2, TRUE),
('Dr. Arjun Verma', 'Neurology', 'LIC20002', 3, TRUE),
('Dr. Priya Shah', 'Orthopedics', 'LIC20003', 4, TRUE),
('Dr. Rohan Kulkarni', 'Dermatology', 'LIC20004', 5, TRUE),
('Dr. Sana Ali', 'Pediatrics', 'LIC20005', 1, TRUE),
('Dr. Vikram Iyer', 'ENT', 'LIC20006', 2, TRUE),
('Dr. Ananya Rao', 'Gynecology', 'LIC20007', 3, TRUE),
('Dr. Karan Gupta', 'Pulmonology', 'LIC20008', 4, TRUE),
('Dr. Neha Bansal', 'Psychiatry', 'LIC20009', 5, TRUE),
('Dr. Sathish Dusharla', 'General Physician', 'MED-2024-SD-001', 6, TRUE);

-- ============================================
-- 3. Insert Staff
-- ============================================
INSERT INTO staff (name, role, employee_id, hospital_id) VALUES
('Jane Smith', 'Nurse', 'EMP001', 1);

-- ============================================
-- 4. Insert Patients
-- ============================================
INSERT INTO patient (name, date_of_birth, gender, contact_info, hospital_id) VALUES
('Alice Johnson', '1990-01-01', 'Female', 'alice@example.com', 1),
('Rahul Mehta', '1988-06-11', 'Male', 'rahul@example.com', 2),
('Divya Menon', '1995-09-18', 'Female', 'divya@example.com', 3),
('Karthik Raj', '1992-03-22', 'Male', 'karthik@example.com', 4),
('Raghu', '1995-05-15', 'Male', 'raghu@example.com', 6);

-- ============================================
-- 5. Insert App Users (Passwords are BCrypt hashed)
-- ============================================
-- Default users (password: "password")
-- BCrypt hash for "password": $2a$10$N9qo8uLOickgx2ZMRZoMye.IQ/1ywCJQAE4B5nqJqQ5m5LzPjZ1ay
-- BCrypt hash for "ram123": $2a$10$xJwL5vCef9/Uf2c4zKqZwuOV/r.0RWLPH5zzMZj5z5z5z5z5z5z5u
-- BCrypt hash for "doctor123": $2a$10$doctor123hashedvaluehere
-- BCrypt hash for "raghu123": $2a$10$raghu123hashedvaluehere

-- NOTE: The actual password hashing is done by Spring Security BCryptPasswordEncoder
-- These are placeholder hashes - the DataLoader will create proper hashed passwords
-- If you want to manually insert users, generate BCrypt hashes first

INSERT INTO app_user (username, password, role, full_name, linked_entity_id) VALUES
-- password for all below is 'password' - hash: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', 'System Administrator', NULL),
('doctor', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'DOCTOR', 'Dr. John Doe', 1),
('patient', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'PATIENT', 'Alice Johnson', 1),
-- Custom demo users
-- password 'ram123' hash: $2a$10$LQv3JXDnMWlVJ8cFOjzPb.CvXwjUQO5x8b5q5X5X5X5X5X5X5X5X
('ram', '$2a$10$LQv3JXDnMWlVJ8cFOjzPb.CvXwjUQO5x8b5q5X5X5X5X5X5X5X5X', 'ADMIN', 'Ram', NULL),
-- password 'doctor123' hash
('drsathish', '$2a$10$EqKPUSMPzRBL.R7R7f8pXuGKVQGEJVF1yyJ4uJ4uJ4uJ4uJ4uJ4u', 'DOCTOR', 'Dr. Sathish Dusharla', 11),
-- password 'raghu123' hash
('raghu', '$2a$10$RYUg7E7aUEPdHU7aUEPdHU7aUEPdHU7aUEPdHU7aUEPdHU7aUEPd', 'PATIENT', 'Raghu', 5);

-- ============================================
-- 6. Insert Diseases
-- ============================================
INSERT INTO disease (name, description) VALUES
('Common Cold', 'Viral infection of the upper respiratory tract'),
('Fever', 'Elevated body temperature due to infection'),
('Diabetes', 'Metabolic disease causing high blood sugar'),
('Hypertension', 'High blood pressure condition'),
('Allergies', 'Immune system reaction to substances');

-- ============================================
-- 7. Insert Medicines
-- ============================================
INSERT INTO medicine (name, description, cost, standard_price, is_expensive) VALUES
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

-- ============================================
-- 8. Link Diseases to Medicines
-- ============================================
INSERT INTO disease_medicine (disease_id, medicine_id) VALUES
(1, 1),  -- Common Cold -> Paracetamol
(1, 2),  -- Common Cold -> Expensive Drug
(2, 1),  -- Fever -> Paracetamol
(2, 9),  -- Fever -> Dolo 650
(2, 10), -- Fever -> Crocin
(3, 6),  -- Diabetes -> Metformin
(4, 14), -- Hypertension -> Atorvastatin
(5, 8),  -- Allergies -> Cetirizine
(5, 13); -- Allergies -> Montelukast

-- ============================================
-- 9. Insert Sample Appointment (Raghu with Dr. Sathish)
-- ============================================
INSERT INTO appointment (
    appointment_date_time,
    symptoms_or_disease,
    status,
    day_patient_number,
    hospital_id,
    doctor_id,
    patient_id
) VALUES (
    NOW() + INTERVAL '1 day',
    'General checkup and fever',
    'SCHEDULED',
    1,
    6,  -- MedTrust
    11, -- Dr. Sathish Dusharla
    5   -- Raghu
);

-- ============================================
-- Verify Data Insertion
-- ============================================
SELECT 'Hospitals' as table_name, COUNT(*) as count FROM hospital
UNION ALL
SELECT 'Doctors', COUNT(*) FROM doctor
UNION ALL
SELECT 'Patients', COUNT(*) FROM patient
UNION ALL
SELECT 'Staff', COUNT(*) FROM staff
UNION ALL
SELECT 'App Users', COUNT(*) FROM app_user
UNION ALL
SELECT 'Medicines', COUNT(*) FROM medicine
UNION ALL
SELECT 'Diseases', COUNT(*) FROM disease
UNION ALL
SELECT 'Appointments', COUNT(*) FROM appointment;
