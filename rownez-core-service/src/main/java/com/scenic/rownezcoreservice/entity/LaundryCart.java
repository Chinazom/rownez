package com.scenic.rownezcoreservice.entity;

import lombok.*;

import javax.persistence.*;
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
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "ROOM_NUMBER",nullable = false)
    private String roomNumber;
    @Column(name = "ITEMS",nullable = false)
    String items;
    @Column(name = "TOTAL")
    double laundryCartTotal;
    /*@Column(name = "express_service",nullable = false)
    private boolean expressService;*/
}
