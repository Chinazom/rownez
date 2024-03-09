package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.LaundryCart;
import com.scenic.rownezcoreservice.entity.LaundryItem;
import com.scenic.rownezcoreservice.model.ItemType;
import com.scenic.rownezcoreservice.model.LaundryCartPojo;
import com.scenic.rownezcoreservice.service_controller.LaundryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Laundry api")
@RequestMapping("/laundry")
public class LaundryController {
    private final LaundryService laundryService;

    public LaundryController(LaundryService laundryService) {
        this.laundryService = laundryService;
    }

    @Operation(summary = "Use this API to get laundry items for a room ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "laundry cart fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @GetMapping()
    public ResponseEntity<Iterable<LaundryItem>> getAllLaundry (){
        return new ResponseEntity<>(laundryService.getAllLaundries(), HttpStatus.OK);
    }
    @Operation(summary = "Use this API to create/ add laundry cart for a room ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "laundry cart created successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @PostMapping("/room/{room-number}")
    public ResponseEntity<String> addLaundryToCart (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = " sample details: (can be used for testing)")
            @PathVariable(name = "room-number") String roomNumber, @RequestBody LaundryCartPojo cart){
        laundryService.addLaundryToCart(roomNumber, cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "Use this API to create/ add laundry cart for a room ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "laundry cart updated successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @PatchMapping("/room/{room-number}")
    public ResponseEntity<String> updateLaundryToCart (@PathVariable(name = "room-number") String roomNumber, @RequestBody LaundryCartPojo cart){
        laundryService.updateLaundryToCart(roomNumber, cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "Use this API to create/ add laundry cart for a room ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "laundry cart fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @GetMapping("/room/{room-number}")
    public ResponseEntity<Optional<LaundryCart>> getLaundryCart (@PathVariable(name = "room-number") String roomNumber){
        return new ResponseEntity<>(laundryService.getLaundryToCart(roomNumber), HttpStatus.OK);
    }
    @Operation(summary = "Use this API to create/ add laundry cart for a room ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "laundry cart deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @DeleteMapping("/room/{room-number}")
    public ResponseEntity<String> clearLaundryCart (@PathVariable(name = "room-number") String roomNumber){
        laundryService.clearLaundryCart(roomNumber);
        return new ResponseEntity<>("Laundry cart cleared", HttpStatus.OK);
    }
    @Operation(summary = "Use this API to create/ add laundry cart for a room ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "laundry item updated successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @PatchMapping("item/cost")
    public ResponseEntity<String> updateLaundryItemCost (@RequestParam ItemType itemType, @RequestParam double cost){
        laundryService.updateLaundryItemPrice(itemType, cost);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
