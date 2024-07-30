package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.CheckIn;
import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomToCheckInMap;
import com.scenic.rownezcoreservice.entity.Staff;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.CheckInRequestDTO;
import com.scenic.rownezcoreservice.repository.CheckInRepo;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.repository.RoomToCheckInMapRepo;
import com.scenic.rownezcoreservice.repository.StaffRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.scenic.rownezcoreservice.service_controller.RestaurantOrderService.INVALID_STAFF_ID;


@Service
@RequiredArgsConstructor
public class CheckInService {
    private final RoomRepo roomRepo;
    private final StaffRepo staffRepo;
    private final CheckInRepo checkInRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;
    private static final String ERROR_MSG_INVALID_ROOM_NUMBER = "Invalid room number";
    private static final String CHECK_IN_DETAILS_NOT_FOUND = "Checking details not found";
    Logger logger = LoggerFactory.getLogger(CheckInService.class);
    @Transactional
    public void addCheckIn(CheckInRequestDTO request) {
        Room  room = roomRepo.findById(request.getRoomNumber()).orElseThrow(() -> new ApiException(ERROR_MSG_INVALID_ROOM_NUMBER, HttpStatus.NOT_FOUND));
        if (request.getStaffId() == null || request.getStaffId().isBlank()){
            throw new ApiException("Check in Staff name required",HttpStatus.BAD_REQUEST);
        }
        if(request.getAmountPaid() <= 0){
            throw new ApiException("please check the amount specified", HttpStatus.BAD_REQUEST);
        }
        if (request.getNumberOfNight() <= 0){
            throw new ApiException("Minimum number of stay is 1 day",HttpStatus.BAD_REQUEST);
        }
        Staff staff = staffRepo.findById(request.getStaffId()).orElseThrow(() -> new ApiException(INVALID_STAFF_ID, HttpStatus.BAD_REQUEST));
        if (request.getGuestName() == null || request.getGuestName().isBlank())
        {
            throw new ApiException("Invalid guest details", HttpStatus.BAD_REQUEST);
        }
        //todo check if the name, email and mobile num is valid. throw error for invalid ones.
        // check if email address  or phone number already exist
        if (!EmailValidator.getInstance().isValid(request.getEmail())){ // checks for invalid email address
            throw new ApiException("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        if ( !Pattern.compile("^\\d{11}$").matcher(request.getPhoneNumber()).matches()) {//validate mobile number check if it contains only 11 digits
            throw new ApiException("Invalid mobile number", HttpStatus.BAD_REQUEST);
        }
        CheckIn checkIn = new CheckIn(room, request.getGuestName(), request.getPhoneNumber(), request.getEmail(), request.getNumberOfGuests(), request.getAmountPaid(), staff.getFirstName() +" "+ staff.getLastName(), request.getNumberOfNight());
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
            throw new ApiException (CHECK_IN_DETAILS_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CheckIn checkOut = checkInRepo.findById(roomToCheckInMap.get().getId()).orElseThrow(()-> new ApiException (CHECK_IN_DETAILS_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR));
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
            throw new ApiException (CHECK_IN_DETAILS_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CheckIn extension = checkInRepo.findById(roomToCheckInMap.get().getId()).orElseThrow(()-> new ApiException (CHECK_IN_DETAILS_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR));
        extension.setNumOfNight(extension.getNumOfNight() + numOfDay);
        extension.setAmountPaid(extension.getAmountPaid() + amountForExtendedNight);
        checkInRepo.save(extension);
    }
}
