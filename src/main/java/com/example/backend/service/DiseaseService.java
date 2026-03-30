package com.example.backend.service;

import com.example.backend.entity.Disease;
import com.example.backend.repository.DiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }

    public Optional<Disease> getDiseaseById(Long id) {
        return diseaseRepository.findById(id);
    }
}
