package com.example.backend.repository;

import com.example.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient_IdOrderByAppointmentDateTimeDesc(Long patientId);
    List<Appointment> findByDoctor_IdOrderByAppointmentDateTimeAsc(Long doctorId);
    boolean existsByPatient_IdAndDoctor_IdAndAppointmentDateTime(Long patientId, Long doctorId, LocalDateTime appointmentDateTime);
    long countByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDoctor_IdAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
        Long doctorId,
        LocalDateTime start,
        LocalDateTime end
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.patient.id = ?1")
    void deleteByPatient_Id(Long patientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = ?1")
    void deleteByDoctor_Id(Long doctorId);

    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.consultation = NULL WHERE a.patient.id = ?1")
    void nullifyConsultationByPatient_Id(Long patientId);

    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.consultation = NULL WHERE a.doctor.id = ?1")
    void nullifyConsultationByDoctor_Id(Long doctorId);
}
