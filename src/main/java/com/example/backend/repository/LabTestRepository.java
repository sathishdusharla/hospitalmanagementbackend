package com.example.backend.repository;

import com.example.backend.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findByConsultation_IdOrderByOrderedAtDesc(Long consultationId);
    List<LabTest> findByConsultation_Patient_IdOrderByOrderedAtDesc(Long patientId);
}
