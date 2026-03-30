package com.example.backend.repository;

import com.example.backend.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatient_Id(Long patientId);
}