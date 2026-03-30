package com.example.backend.service;

import com.example.backend.dto.AppointmentResponse;
import com.example.backend.dto.PatientResponse;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappingService {

    public PatientResponse toPatientResponse(Patient patient) {
        if (patient == null) return null;

        return new PatientResponse(
            patient.getId(),
            patient.getName(),
            patient.getDateOfBirth(),
            patient.getGender(),
            patient.getContactInfo(),
            patient.getHospital() != null ? patient.getHospital().getId() : null,
            patient.getHospital() != null ? patient.getHospital().getName() : null
        );
    }

    public List<PatientResponse> toPatientResponseList(List<Patient> patients) {
        return patients.stream()
            .map(this::toPatientResponse)
            .collect(Collectors.toList());
    }

    public AppointmentResponse toAppointmentResponse(Appointment appointment) {
        if (appointment == null) return null;

        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime());
        response.setSymptomsOrDisease(appointment.getSymptomsOrDisease());
        response.setStatus(appointment.getStatus());
        response.setConsultationFee(appointment.getConsultationFee());
        response.setDayPatientNumber(appointment.getDayPatientNumber());

        // Map patient
        if (appointment.getPatient() != null) {
            response.setPatientId(appointment.getPatient().getId());
            response.setPatientFullName(appointment.getPatient().getName());

            // Parse email and phone from contactInfo
            String contactInfo = appointment.getPatient().getContactInfo();
            if (contactInfo != null && !contactInfo.isEmpty()) {
                String[] parts = contactInfo.split(",");
                if (parts.length >= 1) {
                    String first = parts[0].trim();
                    if (first.contains("@")) {
                        response.setPatientEmail(first);
                        response.setPatientPhone(parts.length >= 2 ? parts[1].trim() : null);
                    } else {
                        response.setPatientPhone(first);
                        response.setPatientEmail(parts.length >= 2 ? parts[1].trim() : null);
                    }
                }
            }
        }

        // Map doctor
        if (appointment.getDoctor() != null) {
            response.setDoctorId(appointment.getDoctor().getId());
            response.setDoctorName(appointment.getDoctor().getName());
            response.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
        }

        // Map hospital
        if (appointment.getHospital() != null) {
            response.setHospitalId(appointment.getHospital().getId());
            response.setHospitalName(appointment.getHospital().getName());
        }

        // Map consultation
        if (appointment.getConsultation() != null) {
            response.setConsultationId(appointment.getConsultation().getId());
        }

        return response;
    }

    public List<AppointmentResponse> toAppointmentResponseList(List<Appointment> appointments) {
        return appointments.stream()
            .map(this::toAppointmentResponse)
            .collect(Collectors.toList());
    }
}
