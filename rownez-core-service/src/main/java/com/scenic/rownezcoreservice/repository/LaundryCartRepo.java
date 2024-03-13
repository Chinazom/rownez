package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.LaundryCart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LaundryCartRepo extends CrudRepository<LaundryCart, UUID> {
    @Modifying
    @Query("UPDATE LaundryCart cart SET cart.items =:items , cart.laundryCartTotal =:total WHERE cart.roomNumber = :roomNumber")
    void updateByItemsAndRoomNumber (String items, double total, String roomNumber);

}
