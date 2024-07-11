package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.RestaurantOrder;
import com.scenic.rownezcoreservice.model.RestaurantOrderDTO;
import com.scenic.rownezcoreservice.service_controller.RestaurantOrderService;
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
@RequestMapping("/restaurant/menu/order")
@Tag(name = "Restaurant Order Controller", description = "Endpoints for managing restaurant orders")
public class RestaurantOrderController {

    private final RestaurantOrderService restaurantOrderService;

    @Autowired
    public RestaurantOrderController(RestaurantOrderService restaurantOrderService) {
        this.restaurantOrderService = restaurantOrderService;
    }
    @Operation(summary = "Place a new Restaurant order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<String> placeOrder(
            @Parameter(description = "Request body containing order details")
            @RequestBody RestaurantOrderDTO restaurantOrderDTO) {
            restaurantOrderService.placeOrder(restaurantOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
    }
    @Operation(summary = "Get an order by table number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{tableNumber}")
    public ResponseEntity<List<RestaurantOrder>> getOrder(
            @Parameter(description = "table number", required = true)
            @PathVariable String tableNumber,
            @Parameter(description = "Staff Id number", required = true)
            @RequestParam String staffId) {
        List<RestaurantOrder> restaurantOrders = restaurantOrderService.getOrdersByTableNotPaid(tableNumber,staffId);
        if (restaurantOrders != null) {
            return ResponseEntity.ok(restaurantOrders);
        }
        return ResponseEntity.notFound().build();
    }
    @Operation(summary = "Get all order by staff id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/staff")
    public ResponseEntity<List<RestaurantOrder>> getOrderByStaff(
            @Parameter(description = "Staff Id number", required = true)
            @RequestParam String staffId) {
        List<RestaurantOrder> restaurantOrders = restaurantOrderService.getOrdersByStaff(staffId);
        if (restaurantOrders != null) {
            return ResponseEntity.ok(restaurantOrders);
        }
        return ResponseEntity.notFound().build();
    }
    @Operation(summary = "update payment status of order by table number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/paid")
    public ResponseEntity<String> updatePaymentStatus(
            @Parameter(description = "table number", required = true)
            @PathVariable String tableNumber,
            @Parameter(description = "Staff Id number", required = true)
            @RequestParam String staffId) {
        boolean paid  = restaurantOrderService.orderPaid(staffId,tableNumber);
        if (paid) {
            return ResponseEntity.ok("order paid for table "+ tableNumber);
        }
        return ResponseEntity.notFound().build();
    }
    @Operation(summary = "Cancel an order by table number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelOrder(
            @Parameter(description = "table number", required = true)
            @PathVariable String tableNumber,
            @Parameter(description = "Staff Id number", required = true)
            @RequestParam String staffId) {
        boolean canceled  = restaurantOrderService.cancelOrder(staffId,tableNumber);
        if (canceled) {
            return ResponseEntity.ok("order canceled for table "+ tableNumber);
        }
        return ResponseEntity.notFound().build();
    }
    @Operation(summary = "update payment status of order by table number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/change-order")
    public ResponseEntity<String> updateOrderRequest(
            @Parameter(description = "Request body containing order details")
            @RequestBody RestaurantOrderDTO restaurantOrderDTO) {
        boolean paid  = restaurantOrderService.updateOrder(restaurantOrderDTO);
        if (paid) {
            return ResponseEntity.ok("Order updated for table "+ restaurantOrderDTO.getTableNumber());
        }
        return ResponseEntity.notFound().build();
    }
}

