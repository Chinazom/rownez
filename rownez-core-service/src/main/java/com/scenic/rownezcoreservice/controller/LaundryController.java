package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.LaundryCart;
import com.scenic.rownezcoreservice.entity.LaundryItem;
import com.scenic.rownezcoreservice.model.ItemType;
import com.scenic.rownezcoreservice.model.LaundryCartPojo;
import com.scenic.rownezcoreservice.serviceController.LaundryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/laundry")
public class LaundryController {
    private final LaundryService laundryService;

    public LaundryController(LaundryService laundryService) {
        this.laundryService = laundryService;
    }

    @GetMapping()
    public ResponseEntity<Iterable<LaundryItem>> getAllLaundry (){
        return new ResponseEntity<>(laundryService.getAllLaundries(), HttpStatus.OK);
    }
    @PostMapping("/room/{room-number}")
    public ResponseEntity<String> addLaundryToCart (@PathVariable(name = "room-number") String roomNumber, @RequestBody LaundryCartPojo cart){
        laundryService.addLaundryToCart(roomNumber, cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PatchMapping("/room/{room-number}")
    public ResponseEntity<String> updateLaundryToCart (@PathVariable(name = "room-number") String roomNumber, @RequestBody LaundryCartPojo cart){
        laundryService.updateLaundryToCart(roomNumber, cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/room/{room-number}")
    public ResponseEntity<Optional<LaundryCart>> getLaundryCart (@PathVariable(name = "room-number") String roomNumber){
        return new ResponseEntity<>(laundryService.getLaundryToCart(roomNumber), HttpStatus.OK);
    }
    @DeleteMapping("/room/{room-number}")
    public ResponseEntity<String> clearLaundryCart (@PathVariable(name = "room-number") String roomNumber){
        laundryService.clearLaundryCart(roomNumber);
        return new ResponseEntity<>("Laundry cart cleared", HttpStatus.OK);
    }
    @PatchMapping("item/cost")
    public ResponseEntity<String> updateLaundryItemCost (@RequestParam ItemType itemType, @RequestParam double cost){
        laundryService.updateLaundryItemPrice(itemType, cost);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
