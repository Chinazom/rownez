package com.scenic.rownezcoreservice.controller.Admin;

import com.scenic.rownezcoreservice.entity.Staff;
import com.scenic.rownezcoreservice.model.StaffDTO;
import com.scenic.rownezcoreservice.service_controller.StaffOnboardingAndOffBoarding;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@Tag(name = "Staff Controller", description = "Endpoints for managing Staff on boarding and off boarding")
public class StaffController {
    private  final StaffOnboardingAndOffBoarding onboardingAndoffBoarding;

    public StaffController(StaffOnboardingAndOffBoarding onboardingAndoffBoarding) {
        this.onboardingAndoffBoarding = onboardingAndoffBoarding;
    }

    @Operation(summary = "Staff on boarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Onboarding successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/onboard")
    public ResponseEntity<String> onboardEmployee(@RequestBody StaffDTO staffDTO) {
        return new ResponseEntity<> (onboardingAndoffBoarding.onBoarding(staffDTO), HttpStatus.CREATED);
    }
    @Operation(summary = "Get Staff details by name ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfull"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/details")
    public ResponseEntity<List<Staff>> getStaffId(@RequestParam String firstName, @RequestParam String lastName) {
        return ResponseEntity.ok(onboardingAndoffBoarding.getStaffIdByName(firstName, lastName));
    }
    @Operation(summary = "Delete Staff Info from the Database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff Info deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Staff Info "),
            @ApiResponse(responseCode = "404", description = "Staff Info not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/offboard/{staffId}")
    public ResponseEntity<String> offBoardStaff(@PathVariable String staffId) {
        onboardingAndoffBoarding.staffOffBoarding (staffId);
        return ResponseEntity.ok("Staff off-boarded successfully");
    }
    @Operation(summary = "Update a Staff info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff Info updated successfully"),
            @ApiResponse(responseCode = "404", description = "Staff Id not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStaffInfo(
            @Parameter(description = "Staff Id") @PathVariable String id,
            @RequestBody StaffDTO staffDTO) {
        boolean updated = onboardingAndoffBoarding.updateStaffInfo(id,staffDTO);
        if (updated) {
            return ResponseEntity.ok("Staff Info updated successfully");
        }
        return ResponseEntity.notFound().build();
    }
}

