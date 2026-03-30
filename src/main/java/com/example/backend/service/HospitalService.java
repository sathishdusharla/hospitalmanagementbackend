package com.example.backend.service;

import com.example.backend.entity.Hospital;
import com.example.backend.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital createHospital(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    public Hospital updateHospital(Long id, Hospital hospitalDetails) throws Exception {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(id);
        if (!hospitalOpt.isPresent()) {
            throw new Exception("Hospital not found");
        }
        Hospital hospital = hospitalOpt.get();
        hospital.setName(hospitalDetails.getName());
        hospital.setAddress(hospitalDetails.getAddress());
        hospital.setPhone(hospitalDetails.getPhone());
        return hospitalRepository.save(hospital);
    }

    public void deleteHospital(Long id) throws Exception {
        if (!hospitalRepository.existsById(id)) {
            throw new Exception("Hospital not found");
        }
        hospitalRepository.deleteById(id);
    }
}