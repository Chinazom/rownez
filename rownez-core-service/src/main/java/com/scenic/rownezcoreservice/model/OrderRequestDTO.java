package com.scenic.rownezcoreservice.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    private String roomNumber;
    private OrderCategory orderCategory;
    private List<ItemOrder> foodOrder;

    // Constructors, getters, and setters
}

