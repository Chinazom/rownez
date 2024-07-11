package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RestaurantTableRepo extends JpaRepository<RestaurantTable, String> {
    Optional<RestaurantTable> findByTableName (String name);
}
