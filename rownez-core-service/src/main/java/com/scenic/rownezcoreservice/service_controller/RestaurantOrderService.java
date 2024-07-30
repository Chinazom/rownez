package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.RestaurantOrder;
import com.scenic.rownezcoreservice.entity.Staff;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.ItemOrder;
import com.scenic.rownezcoreservice.model.OrderStatusDTO;
import com.scenic.rownezcoreservice.model.RestaurantOrderDTO;
import com.scenic.rownezcoreservice.order.state.OrderEvent;
import com.scenic.rownezcoreservice.order.state.OrderState;
import com.scenic.rownezcoreservice.repository.*;
import com.scenic.rownezcoreservice.service.message.MessageServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RestaurantOrderService {
    private static final String INVALID_STAFF_ID = "Invalid Staff Id ";
    private static final String INVALID_FROM_TO_DATE = "Invalid from and to date ";
    private static final String INVALID_TABLE_NUMBER = "Table number is invalid";
    private static final String STAFF_ORDER_EXCEPTION = "Only one Staff can handle table Order";
    private final RestaurantOrderRepo restaurantOrderRepo;
    private final RestaurantTableRepo restaurantTableRepo;
    private final StaffRepo staffRepo;
    private final MessageServiceInterface messageServiceInterface;
    private final StateMachine<OrderState, OrderEvent> stateMachine;

    public RestaurantOrderService(RestaurantOrderRepo restaurantOrderRepo, RestaurantTableRepo restaurantTableRepo, StaffRepo staffRepo, MessageServiceInterface messageServiceInterface, StateMachine<OrderState, OrderEvent> stateMachine) {
        this.restaurantOrderRepo = restaurantOrderRepo;
        this.restaurantTableRepo = restaurantTableRepo;
        this.staffRepo = staffRepo;
        this.messageServiceInterface = messageServiceInterface;
        this.stateMachine = stateMachine;
        this.stateMachine.start();
    }

    /**Todo state transition not yet fully implemented
     * message method not fully implemented
     * table for restaurant order, staff, table number not yet created
     */

    public void placeOrder(RestaurantOrderDTO restaurantOrderDTO) {
        /**
         * check if the table number is valid
         * check if the employee exist
         * check for open order that are not yet paid when a diff employee tries to place new order
         * */
        if (restaurantOrderDTO == null || restaurantOrderDTO.getEmployeeId() == null ||
                restaurantOrderDTO.getEmployeeId().isBlank() || restaurantOrderDTO.getOrderList().isEmpty()){
            throw new ApiException("Invalid parameter", HttpStatus.BAD_REQUEST);
        }
        if (restaurantOrderDTO.getTableNumber() == null ||
                restaurantTableRepo.findByTableName(restaurantOrderDTO.getTableNumber()).isEmpty()){
            throw new ApiException(INVALID_TABLE_NUMBER, HttpStatus.BAD_REQUEST);
        }
        Optional<Staff> staff = staffRepo.findById(restaurantOrderDTO.getEmployeeId());
        if (staff.isEmpty()){
            throw new ApiException(INVALID_STAFF_ID, HttpStatus.BAD_REQUEST);
        }
        // check for existing order by table
        Optional<String> staffId = restaurantOrderRepo.fetchPendingOrderByTable(restaurantOrderDTO.getTableNumber(), false);
        if (staffId.isPresent() && !Objects.equals(restaurantOrderDTO.getEmployeeId(), staffId.get())){
            throw new ApiException("Only one Staff can place order for one table at the time", HttpStatus.BAD_REQUEST);
        }
        for (ItemOrder food : restaurantOrderDTO.getOrderList()) {
            RestaurantOrder restaurantOrder = new RestaurantOrder(food.getQuantity(),food.getItemName(),
                    food.getItemUnitPrice(),food.getItemTotalPrice(), restaurantOrderDTO.getTableNumber(),
                    LocalDateTime.now(), false, food.getItemId(),food.getCategory(), restaurantOrderDTO.getEmployeeId(), OrderState.PENDING);
            restaurantOrderRepo.save(restaurantOrder);
        }
    }

    public List<RestaurantOrder> getOrdersByTableNotPaid(String tableNumber, String staffId) {
        validateRequestParam(staffId,tableNumber);
        return restaurantOrderRepo.findByTableNumberAndStaffIdAndPaid(tableNumber, staffId, false);
    }
    public List<RestaurantOrder> getOrdersByStaff(String staffId, LocalDateTime from, LocalDateTime to) {
        if ( staffId == null ||staffId.isBlank() ){
            throw new ApiException(INVALID_STAFF_ID, HttpStatus.BAD_REQUEST);
        }
        if ((from == null && to != null) || (from != null && to == null)){
            throw new ApiException(INVALID_FROM_TO_DATE, HttpStatus.BAD_REQUEST);
        }
        if (from != null && to != null){
            return restaurantOrderRepo.findByStaffIdAndOrderCreationDateBetween(staffId,from,to);
        }
        return restaurantOrderRepo.findByStaffId(staffId);
    }
    public List<RestaurantOrder> getOrdersByDate( LocalDateTime from, LocalDateTime to) {
        if ((from == null && to != null) || (from != null && to == null)){
            throw new ApiException(INVALID_FROM_TO_DATE, HttpStatus.BAD_REQUEST);
        }
        return restaurantOrderRepo.findAllByOrderCreationDateBetween(from,to);
    }
    public boolean orderPaid (String staffId, String tableNumber ){
        validateRequestParam(staffId, tableNumber);
        List<RestaurantOrder> restaurantOrder = restaurantOrderRepo.findByTableNumberAndStaffIdAndPaid(tableNumber,staffId,false);
        restaurantOrder.forEach(restaurantOrder1 -> {
            restaurantOrder1.setPaid(true);
            restaurantOrderRepo.save(restaurantOrder1);
        });
        return true;
    }

    @Transactional
    public boolean updateOrder(RestaurantOrderDTO restaurantOrderDTO) {
        if (restaurantOrderDTO == null || restaurantOrderDTO.getEmployeeId() == null ||
                restaurantOrderDTO.getEmployeeId().isBlank() || restaurantOrderDTO.getOrderList().isEmpty()){
            throw new ApiException("Invalid parameter", HttpStatus.BAD_REQUEST);
        }
        validateRequestParam(restaurantOrderDTO.getEmployeeId(), restaurantOrderDTO.getTableNumber());
        List<RestaurantOrder> previousOrders = restaurantOrderRepo.findByTableNumberAndStaffIdAndPaid(restaurantOrderDTO.getTableNumber(), restaurantOrderDTO.getEmployeeId(), false);
        List<ItemOrder> itemOrderList = getPreviousOrder(previousOrders);
        // get the difference from the previous order and new order.
        // This is required because the state of the order has to be checked before canceling
        List<ItemOrder> itemOrderDiffOld = getOrderDifference(itemOrderList,restaurantOrderDTO.getOrderList());
        //update the old order
        previousOrders.forEach(restaurantOrder -> {
            if(itemOrderDiffOld.stream().map(ItemOrder::getItemName).toList().contains(restaurantOrder.getItemName())){
             restaurantOrderRepo.delete(restaurantOrder);
            }});
        // add new item from the request
        List<ItemOrder> newItemOrder  = getOrderDifference(restaurantOrderDTO.getOrderList(), itemOrderList);
        newItemOrder.forEach(itemOrder -> {
            RestaurantOrder restaurantOrder = new RestaurantOrder(itemOrder.getQuantity(),itemOrder.getItemName(),
                    itemOrder.getItemUnitPrice(),itemOrder.getItemTotalPrice(), restaurantOrderDTO.getTableNumber(),
                    LocalDateTime.now(), false, itemOrder.getItemId(),itemOrder.getCategory(), restaurantOrderDTO.getEmployeeId(), OrderState.PENDING);
            restaurantOrderRepo.save(restaurantOrder);
        });
        return true;
    }
    @Transactional
    public boolean cancelOrder(String staffId, String tableNumber) {
        validateRequestParam(staffId, tableNumber);
        List<RestaurantOrder> restaurantOrder = restaurantOrderRepo.findByTableNumberAndStaffIdAndPaid(tableNumber,staffId,false);
        restaurantOrder.forEach(restaurantOrder1 -> {
            if (restaurantOrder1.getOrderState() == OrderState.PROCESSING || restaurantOrder1.getOrderState() == OrderState.COMPLETED){
                throw new ApiException("Order cannot be canceled", HttpStatus.CONFLICT);
            }
            if(!stateMachine.sendEvent(OrderEvent.CANCEL)){
                throw new ApiException("Order cannot be cancelled", HttpStatus.CONFLICT);
            }
            restaurantOrder1.setOrderState(stateMachine.getState().getId());
            restaurantOrderRepo.delete(restaurantOrder1);
        });
        return true;
    }

    private void validateRequestParam(String staffId, String tableNumber) {
        if ( staffId == null || staffId.isBlank() ){
            throw new ApiException(INVALID_STAFF_ID, HttpStatus.BAD_REQUEST);
        }
        if ( tableNumber == null || tableNumber.isEmpty() || restaurantTableRepo.findByTableName(tableNumber).isEmpty()){
            throw new ApiException(INVALID_TABLE_NUMBER, HttpStatus.BAD_REQUEST);
        }
        Optional<Staff> staff = staffRepo.findById(staffId);
        if (staff.isEmpty()){
            throw new ApiException(INVALID_STAFF_ID, HttpStatus.BAD_REQUEST);
        }
        // check for existing order by table
        Optional<String> staffInfo = restaurantOrderRepo.fetchPendingOrderByTable(tableNumber, false);
        if (staffInfo.isPresent() &&!Objects.equals(staffInfo.get(), staffId)){
            throw new ApiException(STAFF_ORDER_EXCEPTION, HttpStatus.BAD_REQUEST);
        }
    }
    private List<ItemOrder> getPreviousOrder (List<RestaurantOrder> restaurantOrderList){
        List<ItemOrder> itemOrderList = new ArrayList<>();
        restaurantOrderList.forEach(restaurantOrder -> {
            if (restaurantOrder.getOrderState() != OrderState.PROCESSING || restaurantOrder.getOrderState() != OrderState.COMPLETED|| restaurantOrder.getOrderState() != OrderState.CANCELED) {
                itemOrderList.add(new ItemOrder(restaurantOrder.getItemId(), restaurantOrder.getQuantity(), restaurantOrder.getItemName(), restaurantOrder.getUnitPrice(),
                        restaurantOrder.getItemTotalPrice(), restaurantOrder.getCategory()));
            }else {
                throw new ApiException("order cannot be updated because it has been processed");
            }
        });
        return itemOrderList;
    }
    private List<ItemOrder> getOrderDifference (List<ItemOrder> itemOrderList, List<ItemOrder> itemOrderList1){
        return itemOrderList.stream()
                .filter(element -> !itemOrderList1.contains(element))
                .toList();
    }

    public List<RestaurantOrder> sendKitchenOrder() {
        return restaurantOrderRepo.findByOrderState(OrderState.PENDING);
    }
    //todo consider if it is necessary to make the update status @transactional
    public void updateOrderStatus(List<OrderStatusDTO> orderStatusDTO) {
        List<RestaurantOrder> restaurantOrders = restaurantOrderRepo.findAllByOrderIdIn(orderStatusDTO.stream().map(OrderStatusDTO::getOrderId).toList());
        for (OrderStatusDTO orderDto : orderStatusDTO) {
            RestaurantOrder order = restaurantOrders.stream()
                    .filter(order1 -> order1.getOrderId().equals(orderDto.getOrderId()))
                    .findAny().orElseThrow(() -> new ApiException("The Order Id does not exist", HttpStatus.NOT_FOUND));
            if(!stateMachine.sendEvent(orderDto.getStatus())){
                throw new ApiException("Status update to " +orderDto.getStatus() +" is not allowed", HttpStatus.CONFLICT);
            }
            order.setOrderState(stateMachine.getState().getId());
            restaurantOrderRepo.save(order);
        }
    }
}
