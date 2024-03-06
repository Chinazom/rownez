package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.model.RoomCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepo extends CrudRepository<Room, String> {

    @Modifying
    @Query("UPDATE Room room SET room.price =:price WHERE room.category = :category")
    void updatePrice (@Param("price") double price, @Param("category") RoomCategory category);
    Iterable<Room> findAllByCategoryAndAvailability (RoomCategory category, boolean availability);
    List <Room> findByAvailability (boolean availability);
}
