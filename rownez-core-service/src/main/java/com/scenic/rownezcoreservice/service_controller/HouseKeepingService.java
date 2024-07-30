package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomToBeCleaned;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.repository.RoomToBeCleanedRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseKeepingService {
    private final RoomRepo roomRepo;
    private final RoomToBeCleanedRepo roomToBeCleanedRepo;
    Logger logger = LoggerFactory.getLogger(HouseKeepingService.class);
    // update the rooms to be cleaned
    // @Scheduled(cron = "0/10 * * * * *")
    @Scheduled(cron = "0 0/1 * * * *")
    protected void setRoomsToBeCleaned (){
        logger.info("Fetching room to be cleaned");
        roomToBeCleanedRepo.deleteAll();
        List<String> room = roomRepo.findByAvailability(false).stream().map(Room::getRoomNumber).toList();
        room.forEach(roomToBeCleaned -> roomToBeCleanedRepo.save(new RoomToBeCleaned(roomToBeCleaned)));
    }

    public Iterable<RoomToBeCleaned> roomToBeCleaned() {
        return roomToBeCleanedRepo.findAll();
    }
}
