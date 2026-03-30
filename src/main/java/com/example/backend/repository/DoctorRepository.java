package com.example.backend.repository;

import com.example.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByHospitalId(Long hospitalId);
}