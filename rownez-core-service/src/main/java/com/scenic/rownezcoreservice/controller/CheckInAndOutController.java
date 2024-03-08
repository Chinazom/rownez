package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.entity.RoomToBeCleaned;
import com.scenic.rownezcoreservice.model.CheckInRequestDTO;
import com.scenic.rownezcoreservice.serviceController.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Use this API to create check in for a room ",
    responses = {
            @ApiResponse(responseCode = "201", description = "check in created successfully"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "500", description = "internal server error"),
    })
    @PostMapping("/check-in")
    public ResponseEntity<String> addCheckIn(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = " sample details: (can be used for testing)")
            @RequestBody CheckInRequestDTO request) {
        checkInService.addCheckIn (request);
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>("Check in successfully", HttpStatus.CREATED);
    }
    /**
     * Todo
     * verify if the check out should be done automatically
     * */
    @Operation(summary = "Use this API to check out for a room ",
            responses = {
                    @ApiResponse(responseCode = "201", description = "check out successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @PostMapping("/check-out")
    public ResponseEntity<String> addCheckOut(@RequestParam String roomNumber, @RequestParam String receptionistNameCheckout) {
        checkInService.addCheckOut (roomNumber,receptionistNameCheckout);
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>("Check out successfully", HttpStatus.OK);
    }
    @Operation(summary = "Use this API to extend check out for a room ",
            responses = {
                    @ApiResponse(responseCode = "201", description = "check out extension  created successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @PostMapping("/check-out/extension")
    public ResponseEntity<String> extendCheckOut(@RequestParam String roomNumber, int numOfDay, double amountForExtendedNight) {
        checkInService.extendCheckOut (roomNumber,numOfDay,amountForExtendedNight);
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>("Extension successfully", HttpStatus.CREATED);
    }
    @Operation(summary = "Use this API to fetch rooms tobe cleaned ",
            responses = {
                    @ApiResponse(responseCode = "201", description = "check in created successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @GetMapping("/room-to-be-cleaned")
    public ResponseEntity<Iterable<RoomToBeCleaned>> getListOfRoomTobeCleaned() {
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>(checkInService.roomToBeCleaned (), HttpStatus.OK);
    }
    /*
    * TODO
    *   add check out api
    *  add extend api*/
}
