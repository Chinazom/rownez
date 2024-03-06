package com.scenic.rownezcoreservice.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomPOJO {
    private String number;
    private int capacity;
    private RoomCategory category;
    private double price;
    private int floor;
    boolean availability;
}
