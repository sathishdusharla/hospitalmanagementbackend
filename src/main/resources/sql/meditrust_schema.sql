-- Core tables for new dashboard flows

CREATE TABLE IF NOT EXISTS appointment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  appointment_date_time DATETIME NOT NULL,
  symptoms_or_disease VARCHAR(500),
  status VARCHAR(50) NOT NULL,
  hospital_id BIGINT,
  doctor_id BIGINT,
  patient_id BIGINT,
  CONSTRAINT fk_appointment_hospital FOREIGN KEY (hospital_id) REFERENCES hospital(id),
  CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id),
  CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(id)
);

CREATE TABLE IF NOT EXISTS complaint (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  complaint_type VARCHAR(100) NOT NULL,
  description TEXT,
  reported_amount DECIMAL(12,2),
  status VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL,
  patient_id BIGINT,
  doctor_id BIGINT,
  consultation_id BIGINT,
  prescription_id BIGINT,
  CONSTRAINT fk_complaint_patient FOREIGN KEY (patient_id) REFERENCES patient(id),
  CONSTRAINT fk_complaint_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id),
  CONSTRAINT fk_complaint_consultation FOREIGN KEY (consultation_id) REFERENCES consultation(id),
  CONSTRAINT fk_complaint_prescription FOREIGN KEY (prescription_id) REFERENCES prescription(id)
);

ALTER TABLE hospital ADD COLUMN IF NOT EXISTS approved BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE doctor ADD COLUMN IF NOT EXISTS approved BIT(1) NOT NULL DEFAULT b'1';
