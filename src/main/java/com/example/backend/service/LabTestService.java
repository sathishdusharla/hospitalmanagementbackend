package com.example.backend.service;

import com.example.backend.entity.Consultation;
import com.example.backend.entity.LabTest;
import com.example.backend.repository.ConsultationRepository;
import com.example.backend.repository.LabTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LabTestService {

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public LabTest orderTest(Long consultationId, String testName, String instructions) throws Exception {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new Exception("Consultation not found"));

        LabTest labTest = new LabTest();
        labTest.setConsultation(consultation);
        labTest.setTestName(testName);
        labTest.setInstructions(instructions);
        labTest.setStatus("PENDING");
        labTest.setOrderedAt(LocalDateTime.now());
        return labTestRepository.save(labTest);
    }

    public List<LabTest> getByConsultation(Long consultationId) {
        return labTestRepository.findByConsultation_IdOrderByOrderedAtDesc(consultationId);
    }

    public List<LabTest> getByPatient(Long patientId) {
        return labTestRepository.findByConsultation_Patient_IdOrderByOrderedAtDesc(patientId);
    }

    public LabTest uploadReport(Long labTestId, MultipartFile file) throws Exception {
        LabTest labTest = labTestRepository.findById(labTestId)
            .orElseThrow(() -> new Exception("Lab test not found"));

        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir);
        Files.createDirectories(path);
        Files.write(path.resolve(fileName), file.getBytes());

        labTest.setReportFileName(fileName);
        labTest.setStatus("COMPLETED");
        labTest.setCompletedAt(LocalDateTime.now());
        return labTestRepository.save(labTest);
    }

    public Optional<LabTest> getById(Long id) {
        return labTestRepository.findById(id);
    }
}
