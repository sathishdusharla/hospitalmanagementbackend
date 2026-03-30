package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;
    private boolean approved = false;

    @JsonIgnoreProperties({"hospital", "consultations"})
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Doctor> doctors;

    @JsonIgnoreProperties("hospital")
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Staff> staff;

    @JsonIgnoreProperties("hospital")
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Patient> patients;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public List<Doctor> getDoctors() { return doctors; }
    public void setDoctors(List<Doctor> doctors) { this.doctors = doctors; }

    public List<Staff> getStaff() { return staff; }
    public void setStaff(List<Staff> staff) { this.staff = staff; }

    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }
}