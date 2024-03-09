package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.model.ItemOrder;
import com.scenic.rownezcoreservice.model.OrderCategory;
import com.scenic.rownezcoreservice.model.OrderRequestDTO;
import com.scenic.rownezcoreservice.service_controller.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/menu/order")
@Tag(name = "Order Controller", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Place a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<String> placeOrder(
            @Parameter(description = "Request body containing order details")
            @RequestBody OrderRequestDTO orderRequestDTO) {
        try {
            orderService.placeOrder(orderRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ItemOrder> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable UUID orderId,@Parameter(description = "which category the order is coming from", required = true)
    @PathVariable OrderCategory category) {
        ItemOrder itemOrder = orderService.getOrder(orderId,category);
        if (itemOrder != null) {
            return ResponseEntity.ok(itemOrder);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Update an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<String> updateOrder(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "Request body containing updated order details")
            @RequestBody OrderRequestDTO orderRequestDTO) {
        boolean updated = orderService.updateOrder(orderId, orderRequestDTO);
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
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(
            @Parameter(description = "ID of the order to delete", required = true)
            @PathVariable UUID orderId, @Parameter(description = "which category the order is coming from", required = true)
    @PathVariable OrderCategory category) {
        boolean deleted = orderService.deleteOrder(orderId,category);
        if (deleted) {
            return ResponseEntity.ok("Order deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}

