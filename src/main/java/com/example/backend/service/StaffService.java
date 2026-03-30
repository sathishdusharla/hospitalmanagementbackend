package com.example.backend.service;

import com.example.backend.entity.Staff;
import com.example.backend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }

    public Staff updateStaff(Long id, Staff staffDetails) throws Exception {
        Optional<Staff> staffOpt = staffRepository.findById(id);
        if (!staffOpt.isPresent()) {
            throw new Exception("Staff not found");
        }
        Staff staff = staffOpt.get();
        staff.setName(staffDetails.getName());
        staff.setRole(staffDetails.getRole());
        staff.setEmployeeId(staffDetails.getEmployeeId());
        staff.setHospital(staffDetails.getHospital());
        return staffRepository.save(staff);
    }

    public void deleteStaff(Long id) throws Exception {
        if (!staffRepository.existsById(id)) {
            throw new Exception("Staff not found");
        }
        staffRepository.deleteById(id);
    }
}