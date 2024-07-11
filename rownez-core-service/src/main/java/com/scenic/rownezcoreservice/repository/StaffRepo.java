package com.scenic.rownezcoreservice.repository;


import com.scenic.rownezcoreservice.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepo extends JpaRepository<Staff, String> {
    List<Staff> findByFirstNameAndLastName (String firstName, String lastName);
    Optional<Staff> findByEmailOrPhone (String email, String phone);

    Optional<Staff> findByEmail(String email);

    Optional<Staff> findByPhone(String phone);
}
