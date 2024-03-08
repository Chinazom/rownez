package com.scenic.rownezcoreservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LaundryCartPojo {
    List<String> items;
    double laundryTotal;
    private boolean expressService;


}
