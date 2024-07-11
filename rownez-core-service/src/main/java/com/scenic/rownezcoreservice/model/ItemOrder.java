package com.scenic.rownezcoreservice.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrder {
    long itemId;
    long quantity;
    String itemName;
    double itemUnitPrice;
    double itemTotalPrice;
    MenuCategory category;

}
