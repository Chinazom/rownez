package com.scenic.rownezcoreservice.entity;


import com.scenic.rownezcoreservice.entity.converter.HotelCategoryConverter;
import com.scenic.rownezcoreservice.model.RoomCategory;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class Room {
    @Id
    @Column(name = "NUMBER",nullable = false)
    private String roomNumber;
    @Column(name = "CAPACITY",nullable = false)
    private int capacity;
    @Convert(converter = HotelCategoryConverter.class)
    @Column(name = "CATEGORY",nullable = false)
    private RoomCategory category;
    @Column(name = "PRICE",nullable = false)
    private double price;
    @Column(name = "FLOOR",nullable = false)
    private int floor;
    @Column(name = "AVAILABILITY",nullable = false)
    boolean availability;
    @Column(name = "STORAGE_PATH",nullable = false)
    String storagePath;
}
