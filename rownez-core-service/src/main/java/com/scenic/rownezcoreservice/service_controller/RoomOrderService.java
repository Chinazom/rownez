package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.*;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.OrderRequestDTO;
import com.scenic.rownezcoreservice.model.OrderStatusDTO;
import com.scenic.rownezcoreservice.order.state.OrderEvent;
import com.scenic.rownezcoreservice.order.state.OrderState;
import com.scenic.rownezcoreservice.repository.CheckInRepo;
import com.scenic.rownezcoreservice.repository.RoomOrderRepo;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.repository.RoomToCheckInMapRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.scenic.rownezcoreservice.service_controller.LaundryService.NO_CHECK_IN_ATTACHED_TO_THIS_ROOM;
import static com.scenic.rownezcoreservice.service_controller.LaundryService.ROOM_NUMBER_DOES_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class RoomOrderService {
    private final RoomOrderRepo roomOrderRepo;
    private final RoomRepo roomRepo;
    private final RoomToCheckInMapRepo roomToCheckInMapRepo;
    private final CheckInRepo checkInRepo;
    private final StateMachine<OrderState, OrderEvent> stateMachine;
    @PostConstruct
    private void postConstruct() {
        this.stateMachine.start();
    }


    @Transactional
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
                    itemOrder.getItemUnitPrice(), itemOrder.getItemTotalPrice(), room.getRoomNumber(),
                    LocalDateTime.now(), checkIn, itemOrder.getCategory(), OrderState.PENDING);
            roomOrderRepo.save(order);
            orderIdList.add(order.getOrderId().toString());
        });
        return orderIdList;
    }

    public List<RoomOrder> getOrders(String roomNumber) {
        Room room = roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        return roomOrderRepo.findByCheckInId(String.valueOf(roomToCheckInMap.getId()));
    }

    @Transactional
    public boolean updateOrder(OrderRequestDTO orderRequestDTO,List<UUID>  orderIdList) {
        List <RoomOrder> roomOrderList = roomOrderRepo.findAllByOrderIdIn(orderIdList);
        if(roomOrderList.isEmpty()){
            throw new ApiException("No previous order found", HttpStatus.NOT_FOUND);
        }
        roomOrderList.forEach(roomOrder->{
            if (roomOrder.getOrderState() == OrderState.PROCESSING || roomOrder.getOrderState() == OrderState.COMPLETED ||
                    roomOrder.getOrderState() == OrderState.CANCELED) {
                throw new ApiException("order cannot be updated because it has been processed");
            }
        });
        roomOrderRepo.deleteAllInBatch(roomOrderList);
        placeOrder(orderRequestDTO);
        return true;
    }
    @Transactional
    public boolean cancelOrder(String roomNumber) {
        Room room = roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException(ROOM_NUMBER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND));
        // check for existing check in details
        RoomToCheckInMap roomToCheckInMap = roomToCheckInMapRepo.findByRoomNumber(room.getRoomNumber()).orElseThrow(() -> new ApiException(NO_CHECK_IN_ATTACHED_TO_THIS_ROOM, HttpStatus.NOT_FOUND));
        List<RoomOrder> roomOrderList = roomOrderRepo.findByCheckInId(String.valueOf(roomToCheckInMap.getId()));
        roomOrderList.forEach(roomOrder -> {
            if (roomOrder.getOrderState() == OrderState.PROCESSING || roomOrder.getOrderState() == OrderState.COMPLETED){
                throw new ApiException("Order cannot be canceled", HttpStatus.CONFLICT);
            }
            if(!stateMachine.sendEvent(OrderEvent.CANCEL)){
                throw new ApiException("Order has cannot be cancelled", HttpStatus.CONFLICT);
            }
            roomOrder.setOrderState(stateMachine.getState().getId());
            roomOrderRepo.delete(roomOrder);
        });
        return true;
    }

    public List<RoomOrder> getOrderByIds(List<UUID> orderIds) {
        return  roomOrderRepo.findAllByOrderIdIn(orderIds);
    }
    //todo consider if it is necessary to make the update status @transactional
    public void updateOrderStatus(List<OrderStatusDTO> orderStatusDTOS) {
        List<RoomOrder> roomOrders = roomOrderRepo.findAllByOrderIdIn(orderStatusDTOS.stream().map(OrderStatusDTO::getOrderId).toList());
        for (OrderStatusDTO orderDto : orderStatusDTOS) {
            RoomOrder order = roomOrders.stream()
                    .filter(order1 -> order1.getOrderId().equals(orderDto.getOrderId()))
                    .findAny().orElseThrow(() -> new ApiException("The Order Id does not exist", HttpStatus.NOT_FOUND));
            if(!stateMachine.sendEvent(orderDto.getStatus())){
                throw new ApiException("Status update to " +orderDto.getStatus() +" is not allowed", HttpStatus.CONFLICT);
            }
            order.setOrderState(stateMachine.getState().getId());
            roomOrderRepo.save(order);
        }
    }
}
