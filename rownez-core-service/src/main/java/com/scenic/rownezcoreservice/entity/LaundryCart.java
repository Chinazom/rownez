package com.scenic.rownezcoreservice.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class LaundryCart {
    @Id
    @Column(name = "room_number",nullable = false)
    private String roomNumber;
    @Column(name = "items",nullable = false)
    String items;
    /*@Column(name = "express_service",nullable = false)
    private boolean expressService;*/


}
