package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.RoomToBeCleaned;
import com.scenic.rownezcoreservice.service_controller.HouseKeepingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house-keeping")
public class HouseKeepingController {
    private final HouseKeepingService houseKeepingService;

    public HouseKeepingController(HouseKeepingService houseKeepingService) {
        this.houseKeepingService = houseKeepingService;
    }

    @Operation(summary = "Use this API to fetch rooms tobe cleaned ",
            responses = {
                    @ApiResponse(responseCode = "201", description = "check in created successfully"),
                    @ApiResponse(responseCode = "400", description = "bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "not found"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            })
    @GetMapping
    public ResponseEntity<Iterable<RoomToBeCleaned>> getListOfRoomTobeCleaned() {
        // Return the response with HTTP status 200 OK
        return new ResponseEntity<>(houseKeepingService.roomToBeCleaned (), HttpStatus.OK);
    }

}
