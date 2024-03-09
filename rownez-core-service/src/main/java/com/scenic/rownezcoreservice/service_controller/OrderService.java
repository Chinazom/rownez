package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomOrder;
import com.scenic.rownezcoreservice.entity.RoomToCheckInMap;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.ItemOrder;
import com.scenic.rownezcoreservice.model.OrderCategory;
import com.scenic.rownezcoreservice.model.OrderRequestDTO;
import com.scenic.rownezcoreservice.repository.RoomOrderRepo;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.repository.RoomToCheckInMapRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.scenic.rownezcoreservice.service_controller.LaundryService.NO_CHECK_IN_ATTACHED_TO_THIS_ROOM;
import static com.scenic.rownezcoreservice.service_controller.LaundryService.ROOM_NUMBER_DOES_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final RoomOrderRepo roomOrderRepo;
    private final RoomRepo roomRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;
    public void placeOrder(OrderRequestDTO orderRequestDTO) {
        switch (orderRequestDTO.getOrderCategory()){
            case ROOM_SERVICE -> orderRequestDTO.getFoodOrder().forEach(itemOrder -> {
                if (orderRequestDTO.getRoomNumber()== null||orderRequestDTO.getRoomNumber().isBlank()||itemOrder.getItemName()==null||
                itemOrder.getItemName().isBlank()||itemOrder.getItemUnitPrice()<=0||itemOrder.getQuantity()<=0
                        ||itemOrder.getItemTotalPrice()<=0){
                    throw  new ApiException("one or more of the request parameter is in valid", HttpStatus.BAD_REQUEST);
                }
                Room room =roomRepo.findById(orderRequestDTO.getRoomNumber()).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
                // check for existing check in details
                RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
                RoomOrder order= new RoomOrder(roomToCheckInMap.getId(),itemOrder.getQuantity(), itemOrder.getItemName(),
                        itemOrder.getItemUnitPrice(), itemOrder.getItemTotalPrice(), room.getRoomNumber(), LocalDateTime.now());
                roomOrderRepo.save(order);
            });
            case RESTAURANT_SERVICE -> {
                //todo for food order in the restaurant by outside guest

            }
            default -> throw new ApiException("Unknown order category",HttpStatus.BAD_REQUEST);

        }

    }

    public ItemOrder getOrder(UUID orderId, OrderCategory orderCategory) {
        return null;
    }

    public boolean updateOrder(UUID orderId, OrderRequestDTO orderRequestDTO) {
        switch (orderRequestDTO.getOrderCategory()){
            case ROOM_SERVICE -> orderRequestDTO.getFoodOrder().forEach(itemOrder -> {
              RoomOrder order = roomOrderRepo.findById(orderId).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
                //todo
                roomOrderRepo.save(order);
            });
            case RESTAURANT_SERVICE -> {
                //todo for food order in the restaurant by outside guest

            }
            default -> throw new ApiException("Unknown order category",HttpStatus.BAD_REQUEST);

        }
        return false;
    }

    public boolean deleteOrder(UUID orderId,OrderCategory category) {
        return false;
    }
}
