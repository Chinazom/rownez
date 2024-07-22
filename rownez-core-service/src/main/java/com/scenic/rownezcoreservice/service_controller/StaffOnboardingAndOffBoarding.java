package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.Staff;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.EmailTemplateParam;
import com.scenic.rownezcoreservice.model.EmailTemplateParameterMap;
import com.scenic.rownezcoreservice.model.StaffDTO;
import com.scenic.rownezcoreservice.repository.StaffRepo;
import com.scenic.rownezcoreservice.service.email.EmailServiceInterface;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StaffOnboardingAndOffBoarding {
    private final StaffRepo staffRepo;
    private final EmailServiceInterface emailServiceInterface;

    public StaffOnboardingAndOffBoarding(StaffRepo staffRepo, EmailServiceInterface emailServiceInterface) {
        this.staffRepo = staffRepo;
        this.emailServiceInterface = emailServiceInterface;
    }

    public String onBoarding(StaffDTO staffDTO) throws ApiException {
        if (staffDTO == null){
            throw new ApiException("Request payload is null", HttpStatus.BAD_REQUEST);
        }
        if (staffDTO.getFirstName() == null || staffDTO.getFirstName().isBlank()|| staffDTO.getFirstName().length() < 2||
                staffDTO.getLastName()== null || staffDTO.getLastName().isBlank()||
            staffDTO.getDepartment() == null || staffDTO.getDepartment().isBlank()||
            staffDTO.getEmail() == null || staffDTO.getEmail().isBlank()||
            staffDTO.getPhone() == null || staffDTO.getPhone().isBlank())
        {
            throw new ApiException("Invalid staff details", HttpStatus.BAD_REQUEST);
        }
        //todo check if the name, email and mobile num is valid. throw error for invalid ones.
        // check if email address  or phone number already exist
        if (!EmailValidator.getInstance().isValid(staffDTO.getEmail())){ // checks for invalid email address
            throw new ApiException("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        if ( !Pattern.compile("^\\d{11}$").matcher(staffDTO.getPhone()).matches()) {//validate mobile number check if it contains only 11 digits
            throw new ApiException("Invalid mobile number", HttpStatus.BAD_REQUEST);
        }

        Optional<Staff> existStaff = staffRepo.findByEmailOrPhone(staffDTO.getEmail(), staffDTO.getPhone());
        if (existStaff.isPresent()){
            throw new ApiException("Two staff members cannot have same phone number or email", HttpStatus.BAD_REQUEST);
        }
        String id = generateUsername(staffDTO.getFirstName().toUpperCase());
        Staff staff = new Staff(id.toUpperCase(),staffDTO.getFirstName().toUpperCase(), staffDTO.getLastName().toUpperCase(), staffDTO.getDepartment(),
                staffDTO.getStaffRole(),staffDTO.getPhone(),staffDTO.getDateOfBirth(), LocalDate.now(), staffDTO.getEmail());
        staffRepo.save(staff);
        sendOnboardingEmail (staff);
        return id;
    }
    private String generateUsername (String firstName){
        int count = (int) staffRepo.count();
        if (count == 0){
            count = 1;
        }else {
            count++;
        }
        return  "RZR" + firstName.substring(0,2)+count;
    }
    public List<Staff> getStaffIdByName(String firstName, String lastName) {
        return staffRepo.findByFirstNameAndLastName(firstName.toUpperCase(), lastName.toUpperCase());
    }
    public List<Staff> getAllStaff() {
        return staffRepo.findAll();
    }

    @Transactional
    public void staffOffBoarding(String employeeId) {
        staffRepo.findById(employeeId);
    }
    public boolean updateStaffInfo (String id, StaffDTO staffDTO) throws ApiException{
        Optional<Staff> staff = staffRepo.findById(id);
        if (staff.isPresent()){
            if (staffDTO.getEmail() != null ){
                // check if email address  or phone number already exist
                Optional<Staff> existStaff = staffRepo.findByEmail(staffDTO.getEmail());
                if (existStaff.isPresent() && !existStaff.get().getId().equals(id)){
                    throw new ApiException("Two staff members cannot have same email address", HttpStatus.BAD_REQUEST);
                }
            }
            if (staffDTO.getPhone() != null){
                // check if email address  or phone number already exist
                Optional<Staff> existStaff = staffRepo.findByPhone( staffDTO.getPhone());
                if (existStaff.isPresent() && !existStaff.get().getId().equals(id)){
                    throw new ApiException("Two staff members cannot have same phone number", HttpStatus.BAD_REQUEST);
                }
            }
            setStaffDetails(staffDTO, staff.get());
            staffRepo.save(staff.get());
            return true;
        }else {
            throw new ApiException("No Staff detail found", HttpStatus.BAD_REQUEST);
        }
    }

    private static void setStaffDetails(StaffDTO staffDTO, Staff staff) {
        if (staffDTO.getFirstName() != null && !staffDTO.getFirstName().isBlank()){
            staff.setFirstName(staffDTO.getFirstName().toUpperCase());
        }
        if (staffDTO.getLastName() != null && !staffDTO.getLastName().isBlank()){
        staff.setLastName(staffDTO.getLastName().toUpperCase());
        }
        if (staffDTO.getDepartment() != null && !staffDTO.getDepartment().isBlank()){
        staff.setDepartment(staffDTO.getDepartment());
        }
        if (staffDTO.getStaffRole() != null){
        staff.setStaffRole(staffDTO.getStaffRole());
        }
        if (staffDTO.getEmail() != null && !staffDTO.getEmail().isBlank()){
        staff.setEmail(staffDTO.getEmail());
        }
        if (staffDTO.getPhone() != null && !staffDTO.getPhone().isBlank()){
        staff.setPhone(staffDTO.getPhone());
        }
        if (staffDTO.getDateOfBirth() != null && !staffDTO.getDateOfBirth().isBlank()){
        staff.setDateOfBirth(staffDTO.getPhone());
        }
    }
    private void sendOnboardingEmail (Staff staff){
        EnumMap<EmailTemplateParam, String> emailTemplateParamStringEnumMap = new EnumMap<>(EmailTemplateParam.class);
        emailTemplateParamStringEnumMap.put(EmailTemplateParam.STAFF_NAME, staff.getFirstName());
        emailTemplateParamStringEnumMap.put(EmailTemplateParam.STAFF_ID, staff.getId());
        emailTemplateParamStringEnumMap.put(EmailTemplateParam.STAFF_ROLE, staff.getStaffRole().name());
        emailTemplateParamStringEnumMap.put(EmailTemplateParam.DEPARTMENT, staff.getDepartment());
        emailTemplateParamStringEnumMap.put(EmailTemplateParam.DATE_OF_BIRTH, staff.getDateOfBirth());
        emailTemplateParamStringEnumMap.put(EmailTemplateParam.MOBILE_NUMBER, staff.getPhone());
        EmailTemplateParameterMap.validateTemplateTypeParam(emailTemplateParamStringEnumMap.keySet());
        emailServiceInterface.send(staff.getEmail(), emailTemplateParamStringEnumMap);
    }
}
