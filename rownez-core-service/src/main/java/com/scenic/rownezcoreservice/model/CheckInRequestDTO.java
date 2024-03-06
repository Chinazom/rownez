package com.scenic.rownezcoreservice.model;


import com.scenic.rownezcoreservice.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckInRequestDTO {
    private String roomNumber;
    private String guestName;
    private String phoneNumber;
    private String email;
    private int numberOfGuests;
    private int numberOfNight;
    private String receptionistNameCheckIn;
    private double amountPaid;
}
