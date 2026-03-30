package com.example.backend.repository;

import com.example.backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
	List<Prescription> findByConsultation_Patient_IdOrderByIdDesc(Long patientId);
	List<Prescription> findByConsultation_Doctor_IdOrderByIdDesc(Long doctorId);
}