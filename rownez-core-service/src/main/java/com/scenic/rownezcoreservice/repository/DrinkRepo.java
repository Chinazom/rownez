package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrinkRepo extends JpaRepository<Drink, Long> {
    Optional<Drink> findByName (String name);
}
