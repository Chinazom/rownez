package com.scenic.rownezcoreservice.entity;

import com.scenic.rownezcoreservice.model.MenuCategory;
import com.scenic.rownezcoreservice.orderState.OrderState;
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
    private long itemId;
    private String itemName;

    @Enumerated(EnumType.STRING)
    private MenuCategory category;

    private double unitPrice;

    private double itemTotalPrice;

    private String tableNumber;

    private LocalDateTime orderCreationDate;

    private  String staffId;
    private boolean paid;
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    public RestaurantOrder(long quantity, String itemName, double unitPrice, double itemTotalPrice,
                           String tableNumber, LocalDateTime orderCreationDate, boolean paid, long itemId,
                           MenuCategory category,String staffId, OrderState orderState) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.tableNumber = tableNumber;
        this.orderCreationDate = orderCreationDate;
        this.paid = paid;
        this.itemId = itemId;
        this.category = category;
        this.staffId = staffId;
        this.orderState = orderState;
    }

    @PrePersist
    public void onPrePersist() {
        this.setOrderId(UUID.randomUUID());
    }
}

