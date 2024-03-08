package com.scenic.rownezcoreservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDTO {
    private String name;
    private double price;
    private String category;
    private int prepTime; // in minutes
}