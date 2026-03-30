package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private AlertService alertService;

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    public Prescription createPrescription(Long consultationId, List<Long> medicineIds, String notes) throws Exception {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new Exception("Consultation not found"));

        if (medicineIds == null || medicineIds.isEmpty()) {
            throw new Exception("Select at least one medicine");
        }

        // Guard against duplicate prescription for the same consultation
        if (consultation.getPrescription() != null) {
            throw new Exception("A prescription already exists for this consultation");
        }

        String diagnosis = consultation.getDiagnosis();
        if (diagnosis == null || diagnosis.isBlank()) {
            throw new Exception("Please save diagnosis for this consultation before creating prescription");
        }

        // Efficient disease lookup via repository method
        Disease disease = diseaseRepository.findByNameIgnoreCase(diagnosis)
            .orElseThrow(() -> new Exception("Disease not found for diagnosis: " + diagnosis + ". Please ensure the diagnosis matches a known disease name."));

        List<Medicine> medicines = medicineRepository.findAllById(medicineIds);
        if (medicines.size() != medicineIds.size()) {
            throw new Exception("Some medicines not found");
        }

        Long doctorId = consultation.getDoctor() != null ? consultation.getDoctor().getId() : null;
        Long patientId = consultation.getPatient() != null ? consultation.getPatient().getId() : null;

        // Validate medicines against disease
        List<Medicine> recommended = disease.getRecommendedMedicines();
        for (Medicine med : medicines) {
            if (!recommended.contains(med)) {
                alertService.createAlert("DISEASE_MISMATCH",
                    "Medicine " + med.getName() + " not recommended for " + disease.getName(),
                    "MEDIUM", doctorId, patientId, null);
            }
            if (med.isExpensive()) {
                alertService.createAlert("EXPENSIVE_MEDICINE",
                    "Expensive medicine prescribed: " + med.getName(),
                    "LOW", doctorId, patientId, null);
            }
            if (med.getCost() != null && med.getStandardPrice() != null
                    && med.getCost().compareTo(med.getStandardPrice()) > 0) {
                alertService.createAlert("HIGH_COST_MEDICINE",
                    "Medicine " + med.getName() + " cost higher than standard price",
                    "MEDIUM", doctorId, patientId, null);
            }
        }

        if (medicines.size() > 5) {
            alertService.createAlert("TOO_MANY_MEDICINES",
                "Too many medicines prescribed: " + medicines.size(),
                "HIGH", doctorId, patientId, null);
        }

        BigDecimal totalCost = medicines.stream()
            .map(Medicine::getCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Prescription prescription = new Prescription();
        prescription.setConsultation(consultation);
        prescription.setMedicines(medicines);
        prescription.setNotes(notes);
        prescription.setTotalCost(totalCost);

        Prescription saved = prescriptionRepository.save(prescription);

        // Update alerts with the saved prescription ID
        // (alerts above used null — update the last set if needed; accepted limitation for now)

        // Add to medical history
        String medicineNames = medicines.stream()
            .map(Medicine::getName)
            .collect(Collectors.joining(", "));
        medicalHistoryService.addToHistory(patientId, diagnosis, medicineNames, notes, doctorId);

        return saved;
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }
}
