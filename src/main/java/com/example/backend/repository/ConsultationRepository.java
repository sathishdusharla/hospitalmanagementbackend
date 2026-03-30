package com.example.backend.repository;

import com.example.backend.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
	List<Consultation> findByPatient_IdOrderByDateTimeDesc(Long patientId);
	List<Consultation> findByDoctor_IdOrderByDateTimeDesc(Long doctorId);
}