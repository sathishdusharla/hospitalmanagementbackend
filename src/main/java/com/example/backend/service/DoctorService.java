package com.example.backend.service;

import com.example.backend.entity.Doctor;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalId(hospitalId);
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) throws Exception {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        if (!doctorOpt.isPresent()) {
            throw new Exception("Doctor not found");
        }
        Doctor doctor = doctorOpt.get();
        doctor.setName(doctorDetails.getName());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setLicenseNumber(doctorDetails.getLicenseNumber());
        doctor.setHospital(doctorDetails.getHospital());
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) throws Exception {
        if (!doctorRepository.existsById(id)) {
            throw new Exception("Doctor not found");
        }
        appointmentRepository.nullifyConsultationByDoctor_Id(id);
        appointmentRepository.deleteByDoctor_Id(id);
        doctorRepository.deleteById(id);
        appUserRepository.findByLinkedEntityId(id).ifPresent(appUserRepository::delete);
    }
}