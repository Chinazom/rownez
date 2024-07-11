package com.scenic.rownezcoreservice.entity;

import com.scenic.rownezcoreservice.model.StaffRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String department;
    private StaffRole staffRole;
    private String phone;
    private String dateOfBirth;
    private LocalDate dateJoined;
    private String email;
}
