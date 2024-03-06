package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.RoomToCheckInMap;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomToCheckInMapRepo extends CrudRepository<RoomToCheckInMap, UUID> {
    Optional<RoomToCheckInMap> findByRoomNumber (String roomNumber);
}
