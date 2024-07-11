package com.scenic.rownezcoreservice.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantOrderDTO {
    String tableNumber;
    String employeeId;
    List<ItemOrder> orderList;
}
