package com.scenic.rownezcoreservice.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class LaundryCart {
    @Id
    @Column(name = "ID",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "ROOM_NUMBER",nullable = false)
    private String roomNumber;
    @Column(name = "ITEMS",nullable = false)
    String items;
    @Column(name = "TOTAL")
    double laundryCartTotal;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkIn_id")
    private CheckIn checkIn;
    /*@Column(name = "express_service",nullable = false)
    private boolean expressService;*/

    @PrePersist
    public void onPrePersist() {
        this.setId(UUID.randomUUID());
    }
    public LaundryCart(String roomNumber, String items, double laundryCartTotal, CheckIn checkIn) {
        this.roomNumber = roomNumber;
        this.items = items;
        this.laundryCartTotal = laundryCartTotal;
        this.checkIn = checkIn;
    }
}
