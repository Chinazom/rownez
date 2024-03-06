package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.LaundryCart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LaundryCartRepo extends CrudRepository<LaundryCart, String> {
    @Modifying
    @Query("UPDATE LaundryCart cart SET cart.items =:items WHERE cart.roomNumber = :roomNumber")
    void updateByItemsAndRoomNumber (String items, String roomNumber);
}
