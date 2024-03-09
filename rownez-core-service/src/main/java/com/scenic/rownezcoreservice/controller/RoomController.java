package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.model.RoomCategory;
import com.scenic.rownezcoreservice.service_controller.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService rmService;

    public RoomController(RoomService rmService) {
        this.rmService = rmService;
    }

   /* @PostMapping()
    public ResponseEntity<String> addRoom (@RequestBody RoomPOJO roomPOJO){
        rmService.addRoom(roomPOJO);
        return new ResponseEntity<>("Room added successfully", HttpStatus.CREATED);
    }*/
    //use this to add rooms by category and by floor
    @PostMapping(value = "/floor/category", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE} )
    public ResponseEntity<String> addRoomByCategory (
            @RequestParam RoomCategory category,
            @RequestParam int numOfPeople,
            @RequestParam int numOfRooms,
            @RequestParam List<String> nameOfRoomsList,
            @RequestParam double price,
            @RequestParam int floor,
            @RequestPart("files") MultipartFile[] files){
        rmService.addRoomByFloorAndCategory(category,numOfPeople,numOfRooms,nameOfRoomsList,price,floor,files);
        return new ResponseEntity<>("Rooms added successfully", HttpStatus.CREATED);
    }

    @PatchMapping ()
    public ResponseEntity<String>  updateRoomPrice (@RequestParam RoomCategory category, @RequestParam Double price){
        rmService.updatePriceByCategory (category, price);
        return new ResponseEntity<>("Room Prices successfully updated", HttpStatus.OK);
    }
    @PatchMapping (path = "/update", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE} )
    public ResponseEntity<String>  updateRoomDetails ( @RequestParam RoomCategory category,
                                                       @RequestParam int numOfPeople,
                                                       @RequestParam List<String> nameOfRoomsList,
                                                       @RequestParam double price,
                                                       @RequestParam int floor,
                                                       @RequestPart(value = "files",required = false) MultipartFile[] files){
        rmService.updateRoomDetails (category,numOfPeople,nameOfRoomsList,price,floor,files);
        return new ResponseEntity<>("Room detail successfully updated", HttpStatus.OK);
    }
    @GetMapping (path = "/{room-number}")
    public ResponseEntity<Optional<Room>>  getRoomDetails (@PathVariable(name = "room-number") String roomNumber){
        return new ResponseEntity<>(rmService.getRoomRepo().findById(roomNumber), HttpStatus.OK);
    }
    @GetMapping()
    public ResponseEntity<Iterable<Room>> getAllRooms (){
        return new ResponseEntity<>(rmService.getRoomRepo().findAll(),HttpStatus.OK);
    }
    @Operation(summary = "use this to fetch room by category and also to fetch all room by category that is available")
    @GetMapping(value = "/floor/category")
    public ResponseEntity<Iterable<Room>> getRoomsByCategory (@RequestParam RoomCategory category, @RequestParam boolean availableRoom ){
        return new ResponseEntity<>(rmService.getRoomsByCategoryAndAvailable(category,availableRoom),HttpStatus.OK);
    }

}
