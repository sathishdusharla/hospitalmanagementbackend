package com.example.backend.dto;

public class PatientResponse {
    private Long id;
    private String fullName;  // Maps to name
    private String dateOfBirth;
    private String gender;
    private String email;     // Extracted from contactInfo
    private String phone;     // Extracted from contactInfo
    private String contactInfo; // Full contact info
    private Long hospitalId;
    private String hospitalName;

    public PatientResponse() {}

    public PatientResponse(Long id, String name, String dateOfBirth, String gender, String contactInfo, Long hospitalId, String hospitalName) {
        this.id = id;
        this.fullName = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contactInfo = contactInfo;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        // Parse email and phone from contactInfo
        if (contactInfo != null && !contactInfo.isEmpty()) {
            String[] parts = contactInfo.split(",");
            if (parts.length >= 1) {
                String first = parts[0].trim();
                if (first.contains("@")) {
                    this.email = first;
                    this.phone = parts.length >= 2 ? parts[1].trim() : null;
                } else {
                    this.phone = first;
                    this.email = parts.length >= 2 ? parts[1].trim() : null;
                }
            }
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
}
