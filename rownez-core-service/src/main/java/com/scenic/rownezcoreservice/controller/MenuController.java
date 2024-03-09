package com.scenic.rownezcoreservice.controller;

import com.scenic.rownezcoreservice.entity.Drink;
import com.scenic.rownezcoreservice.entity.Food;
import com.scenic.rownezcoreservice.model.DrinkDTO;
import com.scenic.rownezcoreservice.model.FoodDTO;
import com.scenic.rownezcoreservice.service_controller.DrinkService;
import com.scenic.rownezcoreservice.service_controller.FoodService;
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
import java.util.Map;

@RestController
@RequestMapping("/menu")
@Tag(name = "Menu Controller", description = "Endpoints for managing food and drink items on the menu")
public class MenuController {

    private final FoodService foodService;
    private final DrinkService drinkService;

    @Autowired
    public MenuController(FoodService foodService, DrinkService drinkService) {
        this.foodService = foodService;
        this.drinkService = drinkService;
    }

    @Operation(summary = "Add multiple food items to the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Food items added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/food/add")
    public ResponseEntity<String> addFood(@RequestBody List<FoodDTO> foodDTOList) {
        foodService.addFoodList(foodDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Food items added successfully");
    }

    @Operation(summary = "Update a food item on the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Food item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/food/{id}")
    public ResponseEntity<String> updateFood(
            @Parameter(description = "Food item ID") @PathVariable Long id,
            @RequestBody FoodDTO foodDTO) {
        boolean updated = foodService.updateFood(id, foodDTO);
        if (updated) {
            return ResponseEntity.ok("Food item updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a food item from the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Food item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/food/{id}")
    public ResponseEntity<String> deleteFood(
            @Parameter(description = "Food item ID") @PathVariable Long id) {
        boolean deleted = foodService.deleteFood(id);
        if (deleted) {
            return ResponseEntity.ok("Food item deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all food items from the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved food items"),
            @ApiResponse(responseCode = "404", description = "No food items found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/food")
    public ResponseEntity<Map<String, List<Food>>> getAllFood() {
        Map<String, List<Food>> foodList = foodService.getAllFood();
        if (foodList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foodList);
    }

    @Operation(summary = "Add a drink item to the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Drink item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/drink/add")
    public ResponseEntity<String> addDrink(@RequestBody List<DrinkDTO> drinkDTOs) {
        drinkService.addDrink(drinkDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body("Drink item added successfully");
    }

    @Operation(summary = "Update a drink item on the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drink item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Drink item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/drink/{id}")
    public ResponseEntity<String> updateDrink(
            @Parameter(description = "Drink item ID") @PathVariable Long id,
            @RequestBody DrinkDTO drinkDTO) {
        boolean updated = drinkService.updateDrink(id, drinkDTO);
        if (updated) {
            return ResponseEntity.ok("Drink item updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a drink item from the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drink item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Drink item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/drink/{id}")
    public ResponseEntity<String> deleteDrink(
            @Parameter(description = "Drink item ID") @PathVariable Long id) {
        boolean deleted = drinkService.deleteDrink(id);
        if (deleted) {
            return ResponseEntity.ok("Drink item deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all drink items from the menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved drink items"),
            @ApiResponse(responseCode = "404", description = "No drink items found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/drink")
    public ResponseEntity<Map<String, List<Drink>>> getAllDrinks() {
        Map<String, List<Drink>> drinkList = drinkService.getAllDrinks();
        if (drinkList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(drinkList);
    }
}

