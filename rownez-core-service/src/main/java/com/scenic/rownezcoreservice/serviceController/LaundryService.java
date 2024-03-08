package com.scenic.rownezcoreservice.serviceController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.scenic.rownezcoreservice.entity.*;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.ItemPojo;
import com.scenic.rownezcoreservice.model.ItemType;
import com.scenic.rownezcoreservice.model.LaundryCartPojo;
import com.scenic.rownezcoreservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class LaundryService {
    public static final String ROOM_NUMBER_DOES_NOT_EXIST = "The room number does not exist";
    public static final String NO_CHECK_IN_ATTACHED_TO_THIS_ROOM = "No check in attached to this room";
    private final LaundryItemRepo laundryItemRepo;
    private final LaundryCartRepo laundryCartRepo;
    private final RoomRepo roomRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;

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

    public void addLaundryToCart(String roomNumber, LaundryCartPojo cart) {
        Room room =roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(roomNumber).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        // check for existing cart
        Optional<LaundryCart> laundryCartOptional = laundryCartRepo.findById(roomToCheckInMap.getId());
        // create new cart when no cart is found
        if(laundryCartOptional.isEmpty()){
            laundryCartRepo.save(new LaundryCart(roomToCheckInMap.getId(), room.getRoomNumber(), cart.getItems().toString(), cart.getLaundryTotal()));
        }else {
            // update existing cart
            try {

                List<String> updatedItems = cart.getItems();
                ObjectMapper mapper = new ObjectMapper();
                List<ItemPojo> oldCartItem = mapper.readValue(laundryCartOptional.get().getItems(), TypeFactory.defaultInstance().constructCollectionType(List.class, ItemPojo.class));
                for(ItemPojo itemPojo: oldCartItem){
                    String itemStr = mapper.writeValueAsString(itemPojo);
                    updatedItems.add(itemStr);
                }
                laundryCartOptional.get().setItems(updatedItems.toString());
                laundryCartRepo.save(laundryCartOptional.get());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

    }
    @Transactional
    public void updateLaundryToCart(String roomNumber, LaundryCartPojo cart) {
        Room room =roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        LaundryCart laundryCart = laundryCartRepo.findById(roomToCheckInMap.getId()).orElseThrow(()-> new ApiException("Laundry basket is empty", HttpStatus.NOT_FOUND));
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
