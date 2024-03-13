package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.*;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.ItemType;
import com.scenic.rownezcoreservice.model.LaundryCartPojo;
import com.scenic.rownezcoreservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LaundryService {
    public static final String ROOM_NUMBER_DOES_NOT_EXIST = "The room number does not exist";
    public static final String NO_CHECK_IN_ATTACHED_TO_THIS_ROOM = "No check in attached to this room";
    private final LaundryItemRepo laundryItemRepo;
    private final LaundryCartRepo laundryCartRepo;
    private final RoomRepo roomRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;
    private final CheckInRepo checkInRepo;

    public Iterable<LaundryItem> getAllLaundries() {
        return laundryItemRepo.findAll();
    }
    public void updateLaundryItemPrice (ItemType item, double cost){
        Optional<LaundryItem> laundryItem = laundryItemRepo.findById(item);
        if (laundryItem.isEmpty()){
            throw new ApiException("Laundry Item not found");
        }
        laundryItem.get().setCost(cost);
        laundryItemRepo.save(laundryItem.get());
    }

    public String addLaundryToCart(String roomNumber, LaundryCartPojo cart) {
        Room room =roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(roomNumber).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        CheckIn checkIn = checkInRepo.findById(roomToCheckInMap.getId()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        LaundryCart laundryCart = new LaundryCart(room.getRoomNumber(), cart.getItems().toString(), cart.getLaundryTotal(), checkIn);
        laundryCartRepo.save(laundryCart);
        return String.valueOf(laundryCart.getId());
    }
    @Transactional
    public void updateLaundryToCart( UUID laundryOrderId, LaundryCartPojo cart) {
       LaundryCart laundryCart = laundryCartRepo.findById(laundryOrderId).orElseThrow(()-> new ApiException("Laundry basket is empty", HttpStatus.NOT_FOUND));
       laundryCartRepo.updateByItemsAndRoomNumber(cart.getItems().toString(),cart.getLaundryTotal(), laundryCart.getRoomNumber());
    }
    public Optional<LaundryCart> getLaundryToCart(String roomNumber) {
        Room room =roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        return laundryCartRepo.findById(roomToCheckInMap.getId());
    }
    @Transactional
    public void clearLaundryCart(String roomNumber) {
        Room room =roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        LaundryCart cart = laundryCartRepo.findById(roomToCheckInMap.getId()).orElseThrow(()-> new ApiException("Laundry basket is empty", HttpStatus.NOT_FOUND));
        laundryCartRepo.deleteById(cart.getId());
    }
}
