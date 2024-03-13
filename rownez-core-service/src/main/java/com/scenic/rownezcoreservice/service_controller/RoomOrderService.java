package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.CheckIn;
import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomOrder;
import com.scenic.rownezcoreservice.entity.RoomToCheckInMap;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.OrderRequestDTO;
import com.scenic.rownezcoreservice.repository.CheckInRepo;
import com.scenic.rownezcoreservice.repository.RoomOrderRepo;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.repository.RoomToCheckInMapRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.scenic.rownezcoreservice.service_controller.LaundryService.NO_CHECK_IN_ATTACHED_TO_THIS_ROOM;
import static com.scenic.rownezcoreservice.service_controller.LaundryService.ROOM_NUMBER_DOES_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class RoomOrderService {
    private final RoomOrderRepo roomOrderRepo;
    private final RoomRepo roomRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;
    private final CheckInRepo checkInRepo;

    public List<String> placeOrder(OrderRequestDTO orderRequestDTO) {
        List<String>  orderIdList = new ArrayList<>();
        Room room = roomRepo.findById(orderRequestDTO.getRoomNumber()).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        CheckIn checkIn = checkInRepo.findById(roomToCheckInMap.getId()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));

        orderRequestDTO.getFoodOrder().forEach(itemOrder -> {
            if (orderRequestDTO.getRoomNumber() == null || orderRequestDTO.getRoomNumber().isBlank() || itemOrder.getItemName() == null ||
                    itemOrder.getItemName().isBlank() || itemOrder.getItemUnitPrice() <= 0 || itemOrder.getQuantity() <= 0
                    || itemOrder.getItemTotalPrice() <= 0) {
                throw new ApiException("one or more of the request parameter is in valid", HttpStatus.BAD_REQUEST);
            }
            RoomOrder order = new RoomOrder(itemOrder.getQuantity(), itemOrder.getItemName(),
                    itemOrder.getItemUnitPrice(), itemOrder.getItemTotalPrice(), room.getRoomNumber(), LocalDateTime.now(), checkIn);
            roomOrderRepo.save(order);
            orderIdList.add(order.getOrderId().toString());
        });
        return orderIdList;
    }

    public List<RoomOrder> getOrder(String roomNumber) {
        Room room = roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        return roomOrderRepo.findByCheckInId(String.valueOf(roomToCheckInMap.getId()));
    }

    @Transactional
    public boolean updateOrder(OrderRequestDTO orderRequestDTO,List<String>  orderIdList) {
        List <RoomOrder> roomOrderList = roomOrderRepo.findAllByOrderIdIn(orderIdList);
        roomOrderRepo.deleteAll(roomOrderList);
        placeOrder(orderRequestDTO);
        return true;
    }
    @Transactional
    public boolean deleteOrder(String roomNumber) {
        Room room = roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        List<RoomOrder> roomOrderList = roomOrderRepo.findByCheckInId(String.valueOf(roomToCheckInMap.getId()));
        roomOrderList.forEach(roomOrderRepo::delete);
        return true;
    }
}
