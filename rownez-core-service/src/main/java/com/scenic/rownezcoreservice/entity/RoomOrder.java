package com.scenic.rownezcoreservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomOrder {

    @Id
    @Column(name = "orderId",nullable = false)
    private UUID orderId;

    private long quantity;

    private String itemName;

    private double unitPrice;

    private double itemTotalPrice;

    private String roomNumber;

    private LocalDateTime orderCreationDate;


}

