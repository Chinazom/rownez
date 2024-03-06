package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.CheckIn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CheckInRepo extends CrudRepository<CheckIn, UUID> {
    /*@Query("SELECT chkIn.roomNumber.roomNumber FROM CheckIn AS chkIn WHERE chkIn.checkOutDate = :checkOutDate")
    List<String> findListOfRoomTobeCleaned (LocalDateTime checkOutDate);*/
}
