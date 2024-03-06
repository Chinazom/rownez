package com.scenic.rownezcoreservice.serviceController;

import com.scenic.rownezcoreservice.entity.CheckIn;
import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomToBeCleaned;
import com.scenic.rownezcoreservice.entity.RoomToCheckInMap;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.CheckInRequestDTO;
import com.scenic.rownezcoreservice.repository.CheckInRepo;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.repository.RoomToBeCleanedRepo;
import com.scenic.rownezcoreservice.repository.RoomToCheckInMapRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CheckInService {
    private final RoomRepo roomRepo;
    private final CheckInRepo checkInRepo;
    private final RoomToBeCleanedRepo roomToBeCleanedRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;
    private static final String ERROR_MSG_INVALID_ROOM_NUMBER = "Invalid room number";
    Logger logger = LoggerFactory.getLogger(CheckInService.class);
    @Transactional
    public void addCheckIn(CheckInRequestDTO request) {
        Room  room = roomRepo.findById(request.getRoomNumber()).orElseThrow(() -> new ApiException(ERROR_MSG_INVALID_ROOM_NUMBER, HttpStatus.NOT_FOUND));
        if (request.getReceptionistNameCheckIn() == null || request.getReceptionistNameCheckIn().isBlank()){
            throw new ApiException("Check in Agent name required",HttpStatus.BAD_REQUEST);
        }
        if(request.getAmountPaid() <= 0){
            throw new ApiException("please check the amount specified", HttpStatus.BAD_REQUEST);
        }
        if (request.getNumberOfNight() <= 0){
            throw new ApiException("Minimum number of stay is 1 day",HttpStatus.BAD_REQUEST);
        }
        CheckIn checkIn = new CheckIn(room, request.getGuestName(), request.getPhoneNumber(), request.getEmail(), request.getNumberOfGuests(), request.getAmountPaid(), request.getReceptionistNameCheckIn(), request.getNumberOfNight());
        checkInRepo.save(checkIn);
        // set the uuid to room-to-check-in-map table. This is important for fetching the id during check out
        RoomToCheckInMap roomToCheckInMap = new RoomToCheckInMap(checkIn.getId(), room.getRoomNumber());
        roomToCheckInMapRepo.save(roomToCheckInMap);
        // updates the status of the room to not available on check in
        room.setAvailability(false);
        roomRepo.save(room);

    }

    @Transactional
    public void addCheckOut(String roomNumber, String receptionistNameCheckout) {
        if (roomNumber == null || roomNumber.isBlank()){
            throw new ApiException(ERROR_MSG_INVALID_ROOM_NUMBER,HttpStatus.BAD_REQUEST);
        }
        if (receptionistNameCheckout == null || receptionistNameCheckout.isBlank()){
            throw new ApiException("Agents name required",HttpStatus.BAD_REQUEST);
        }
        Room  room = roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ERROR_MSG_INVALID_ROOM_NUMBER, HttpStatus.NOT_FOUND));
        //fetch the last check in id for the room
        Optional<RoomToCheckInMap> roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(roomNumber);
        if (roomToCheckInMap.isEmpty()){
            throw new ApiException ("Checking details not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CheckIn checkOut = checkInRepo.findById(roomToCheckInMap.get().getId()).orElseThrow(()-> new ApiException ("Checking details not found", HttpStatus.INTERNAL_SERVER_ERROR));
        checkOut.setCheckOutDate(LocalDateTime.now());
        checkOut.setReceptionistNameCheckout(receptionistNameCheckout);
        checkInRepo.save(checkOut);
        // updates the status of the room to available on check out
        room.setAvailability(true);
        roomRepo.save(room);
        // delete the entry in the roomToCheckinMap
        roomToCheckInMapRepo.delete(roomToCheckInMap.get());

    }


    //TODO verify to know how rooms are booked on hold
    public void extendCheckOut(String roomNumber, int numOfDay, double amountForExtendedNight) {
        if (roomNumber == null || roomNumber.isBlank()){
            throw new ApiException(ERROR_MSG_INVALID_ROOM_NUMBER,HttpStatus.BAD_REQUEST);
        }
        if (numOfDay <= 0){
            throw new ApiException("Minimum number of stay is 1 day",HttpStatus.BAD_REQUEST);
        }
        if(amountForExtendedNight <= 0){
            throw new ApiException("please check the amount specified", HttpStatus.BAD_REQUEST);
        }
        Optional<Room>  room = roomRepo.findById(roomNumber);
        if (room.isEmpty()){
            throw new ApiException(ERROR_MSG_INVALID_ROOM_NUMBER, HttpStatus.NOT_FOUND);
        }
        //fetch the last check in id for the room
        Optional<RoomToCheckInMap> roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(roomNumber);
        if (roomToCheckInMap.isEmpty()){
            throw new ApiException ("Checking details not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CheckIn extension = checkInRepo.findById(roomToCheckInMap.get().getId()).orElseThrow(()-> new ApiException ("Checking details not found", HttpStatus.INTERNAL_SERVER_ERROR));
        extension.setNumOfNight(extension.getNumOfNight() + numOfDay);
        extension.setAmountPaid(extension.getAmountPaid() + amountForExtendedNight);
        checkInRepo.save(extension);
    }

    // update the rooms to be cleaned
    @Scheduled(cron = "0/10 * * * * *")
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
