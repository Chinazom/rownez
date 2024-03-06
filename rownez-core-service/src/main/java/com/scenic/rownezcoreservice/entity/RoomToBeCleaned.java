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
public class RoomToBeCleaned {
    @Id
    @Column(name = "ID",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ROOM_NUMBER")
    private String roomNumber;

    public RoomToBeCleaned(String roomToBeCleaned) {
        this.roomNumber =roomToBeCleaned;
    }

}
