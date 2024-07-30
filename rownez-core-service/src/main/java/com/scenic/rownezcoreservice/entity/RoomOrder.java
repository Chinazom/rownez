package com.scenic.rownezcoreservice.entity;

import com.scenic.rownezcoreservice.model.MenuCategory;
import com.scenic.rownezcoreservice.order.state.OrderState;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    private long quantity;

    private String itemName;

    private double unitPrice;

    private double itemTotalPrice;

    @Enumerated(EnumType.STRING)
    private MenuCategory category;

    private String roomNumber;

    private LocalDateTime orderCreationDate;
    @Enumerated(EnumType.STRING)
    private OrderState orderState;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkIn_id")
    private CheckIn checkIn;

    public RoomOrder(long quantity, String itemName, double unitPrice, double itemTotalPrice,
                     String roomNumber, LocalDateTime orderCreationDate, CheckIn checkInId,
                     MenuCategory category, OrderState orderState) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.roomNumber = roomNumber;
        this.orderCreationDate = orderCreationDate;
        this.checkIn = checkInId;
        this.category = category;
        this.orderState = orderState;
    }

    @PrePersist
    public void onPrePersist() {
        this.setOrderId(UUID.randomUUID());
    }
}

