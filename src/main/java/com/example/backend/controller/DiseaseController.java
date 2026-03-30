package com.example.backend.controller;

import com.example.backend.entity.Disease;
import com.example.backend.service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @GetMapping
    public ResponseEntity<List<Disease>> getAllDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Disease> getDiseaseById(@PathVariable Long id) {
        return diseaseService.getDiseaseById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
