-- Patient dashboard
SELECT * FROM appointment WHERE patient_id = ? ORDER BY appointment_date_time DESC;
SELECT c.* FROM consultation c WHERE c.patient_id = ? ORDER BY c.date_time DESC;
SELECT p.*
FROM prescription p
JOIN consultation c ON c.id = p.consultation_id
WHERE c.patient_id = ?
ORDER BY p.id DESC;

-- Doctor dashboard
SELECT *
FROM appointment
WHERE doctor_id = ?
  AND appointment_date_time >= CURDATE()
  AND appointment_date_time < DATE_ADD(CURDATE(), INTERVAL 1 DAY)
ORDER BY appointment_date_time;

SELECT COUNT(DISTINCT patient_id) AS total_patients_treated
FROM consultation
WHERE doctor_id = ?;

-- Admin analytics
SELECT m.name, COUNT(*) AS total_prescribed
FROM prescription_medicine pm
JOIN medicine m ON m.id = pm.medicine_id
GROUP BY m.name
ORDER BY total_prescribed DESC;

SELECT d.name AS doctor_name, SUM(p.total_cost) AS total_prescription_cost
FROM prescription p
JOIN consultation c ON c.id = p.consultation_id
JOIN doctor d ON d.id = c.doctor_id
GROUP BY d.name
ORDER BY total_prescription_cost DESC;

SELECT h.name AS hospital_name, COUNT(a.id) AS alerts_count
FROM alert a
JOIN doctor d ON d.id = a.doctor_id
JOIN hospital h ON h.id = d.hospital_id
GROUP BY h.name
ORDER BY alerts_count DESC;

SELECT DATE_FORMAT(c.date_time, '%Y-%m') AS month, COUNT(*) AS visits
FROM consultation c
GROUP BY DATE_FORMAT(c.date_time, '%Y-%m')
ORDER BY month;

-- Complaints
INSERT INTO complaint (complaint_type, description, reported_amount, status, created_at, patient_id, doctor_id, consultation_id, prescription_id)
VALUES (?, ?, ?, 'OPEN', NOW(), ?, ?, ?, ?);

SELECT * FROM complaint ORDER BY created_at DESC;
UPDATE complaint SET status = 'RESOLVED' WHERE id = ?;
