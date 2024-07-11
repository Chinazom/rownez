package com.scenic.rownezcoreservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO {
    private String firstName;
    private String lastName;
    private String department;
    private StaffRole staffRole;
    private String phone;
    private String dateOfBirth;
    private String email;
}