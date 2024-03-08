package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
    Optional <Food> findByName (String name);
}
