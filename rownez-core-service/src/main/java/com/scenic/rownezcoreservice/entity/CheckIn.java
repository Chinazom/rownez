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
public class CheckIn {
    @Id
    @Column(name = "ID",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ROOM_NUMBER", referencedColumnName = "NUMBER")
    private Room roomNumber;
    @Column(name = "GUEST_NAME")
    private String guestName;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "NUMBER_OF_GUEST")
    private int numberOfGuests;
    @Column(name = "CHECK_IN_DATE", nullable = false)
    private LocalDateTime checkInDate;
    @Column(name = "CHECK_OUT_DATE")
    private LocalDateTime checkOutDate;
    @Column(name = "AMOUNT_PAID", nullable = false)
    private double amountPaid;
    @Column(name = "CHECK_IN_AGENT",nullable = false)
    private String receptionistNameCheckIn;
    @Column(name = "CHECK_OUT_AGENT")
    private String receptionistNameCheckout;
    @Column(name = "NUMBER_OF_NIGHT", nullable = false)
    private int numOfNight;


    public CheckIn(Room roomNumber, String guestName, String phoneNumber, String email, int numberOfGuests,  double amountPaid, String receptionistNameCheckIn, int numOfNight) {
        this.roomNumber = roomNumber;
        this.guestName = guestName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.numberOfGuests = numberOfGuests;
        this.amountPaid = amountPaid;
        this.receptionistNameCheckIn = receptionistNameCheckIn;
        this.numOfNight = numOfNight;
    }
    @PrePersist
    public void onPrePersist() {
        this.setId(UUID.randomUUID());
        this.setCheckInDate(LocalDateTime.now());
    }
}
