package com.scenic.rownezcoreservice.entity;

import com.scenic.rownezcoreservice.entity.converter.ItemTypeConverter;
import com.scenic.rownezcoreservice.model.ItemType;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LaundryItem {
    @Id
    @Column(name = "item",nullable = false)
    //@Convert(converter = ItemTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private ItemType item;
    @Column(name = "cost",nullable = false)
    private double cost;
}
