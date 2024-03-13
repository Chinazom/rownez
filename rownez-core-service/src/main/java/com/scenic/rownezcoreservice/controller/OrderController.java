package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.RoomOrder;
import com.scenic.rownezcoreservice.model.OrderCategory;
import com.scenic.rownezcoreservice.model.OrderRequestDTO;
import com.scenic.rownezcoreservice.service_controller.RoomOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu/order")
@Tag(name = "Order Controller", description = "Endpoints for managing orders")
public class OrderController {

    private final RoomOrderService roomOrderService;

    @Autowired
    public OrderController(RoomOrderService roomOrderService) {
        this.roomOrderService = roomOrderService;
    }

    @Operation(summary = "Place a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<List<String>> placeOrder(
            @Parameter(description = "Request body containing order details")
            @RequestBody OrderRequestDTO orderRequestDTO) {
        try {
           List<String> orderIds = roomOrderService.placeOrder(orderRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderIds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{roomNumber}")
    public ResponseEntity<List<RoomOrder>> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable String roomNumber,
            @Parameter(description = "which category the order is coming from", required = true)
    @PathVariable OrderCategory category) {
        List<RoomOrder> roomOrderList= roomOrderService.getOrder(roomNumber);
        if (roomOrderList != null) {
            return ResponseEntity.ok(roomOrderList);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Update an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping()
    public ResponseEntity<String> updateOrder(
            @RequestParam List<String> orderIds,
            @RequestBody OrderRequestDTO orderRequestDTO) {
        boolean updated = roomOrderService.updateOrder(orderRequestDTO,orderIds);
        if (updated) {
            return ResponseEntity.ok("Order updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{roomNumber}")
    public ResponseEntity<String> deleteOrder(
            @Parameter(description = "Room number of the order to delete", required = true)
            @PathVariable String roomNumber, @Parameter(description = "which category the order is coming from", required = true)
    @PathVariable OrderCategory category) {
        boolean deleted = roomOrderService.deleteOrder(roomNumber);
        if (deleted) {
            return ResponseEntity.ok("Order deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}

