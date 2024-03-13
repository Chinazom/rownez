package com.scenic.rownezcoreservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Restaurant_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrder {

    @Id
    @Column(name = "orderId",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    private long quantity;

    private String itemName;

    private double unitPrice;

    private double itemTotalPrice;

    private String tableNumber;

    private LocalDateTime orderCreationDate;

    public RestaurantOrder(long quantity, String itemName, double unitPrice, double itemTotalPrice,
                           String tableNumber, LocalDateTime orderCreationDate) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.tableNumber = tableNumber;
        this.orderCreationDate = orderCreationDate;
    }

    @PrePersist
    public void onPrePersist() {
        this.setOrderId(UUID.randomUUID());
    }
}

