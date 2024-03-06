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
public class RoomToCheckInMap {
    // use this table to get the updated check in id for a room

    @Id
    @Column(name = "CHECK_IN_ID",nullable = false)
    private UUID id;

    @Column(name = "ROOM_NUMBER",nullable = false)
    private String roomNumber;

}
