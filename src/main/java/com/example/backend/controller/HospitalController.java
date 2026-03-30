package com.example.backend.controller;

import com.example.backend.entity.Hospital;
import com.example.backend.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<Hospital> createHospital(@RequestBody Hospital hospital) {
        return ResponseEntity.ok(hospitalService.createHospital(hospital));
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
        return hospitalService.getHospitalById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        try {
            return ResponseEntity.ok(hospitalService.updateHospital(id, hospital));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        try {
            hospitalService.deleteHospital(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}