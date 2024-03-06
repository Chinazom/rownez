package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomToBeCleaned;
import com.scenic.rownezcoreservice.model.CheckInRequestDTO;
import com.scenic.rownezcoreservice.serviceController.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal")
public class CheckInAndOutController {
    private final CheckInService checkInService;

    public CheckInAndOutController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<String> addCheckIn(@RequestBody CheckInRequestDTO request) {
        checkInService.addCheckIn (request);
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>("Check in successfully", HttpStatus.CREATED);
    }
    /**
     * Todo
     * verify if the check out should be done automatically
     * */
    @PostMapping("/check-out")
    public ResponseEntity<String> addCheckOut(@RequestParam String roomNumber, @RequestParam String receptionistNameCheckout) {
        checkInService.addCheckOut (roomNumber,receptionistNameCheckout);
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>("Check out successfully", HttpStatus.CREATED);
    }
    @PostMapping("/extension/check-out")
    public ResponseEntity<String> extendCheckOut(@RequestParam String roomNumber, int numOfDay, double amountForExtendedNight) {
        checkInService.extendCheckOut (roomNumber,numOfDay,amountForExtendedNight);
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>("Extension successfully", HttpStatus.CREATED);
    }
    @PostMapping("/room-to-be-cleaned")
    public ResponseEntity<Iterable<RoomToBeCleaned>> getListOfRoomTobeCleaned() {
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>(checkInService.roomToBeCleaned (), HttpStatus.OK);
    }
    /*
    * TODO
    *   add check out api
    *  add extend api*/
}
