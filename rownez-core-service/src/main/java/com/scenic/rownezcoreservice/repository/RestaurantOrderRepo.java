package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantOrderRepo extends JpaRepository<RestaurantOrder, UUID> {

    List<RestaurantOrder> findByTableNumberAndStaffIdAndPaid(String tableName, String staffId, boolean paid);
    List<RestaurantOrder> findByStaffId( String staffId);
    @Query("Select order.staffId From RestaurantOrder as order  WHERE order.tableNumber =:tableNumber AND order.paid =:paid")
    Optional<String> fetchPendingOrderByTable(String tableNumber, boolean paid);
}

