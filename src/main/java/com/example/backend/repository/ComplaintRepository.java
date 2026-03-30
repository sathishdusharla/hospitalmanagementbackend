package com.example.backend.repository;

import com.example.backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPatient_IdOrderByCreatedAtDesc(Long patientId);
}
