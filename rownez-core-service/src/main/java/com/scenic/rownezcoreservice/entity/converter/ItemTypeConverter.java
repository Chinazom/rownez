package com.scenic.rownezcoreservice.entity.converter;

import com.scenic.rownezcoreservice.model.ItemType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ItemTypeConverter implements AttributeConverter <ItemType,String> {
    @Override
    public String convertToDatabaseColumn(ItemType itemType) {
        return itemType.name();
    }

    @Override
    public ItemType convertToEntityAttribute(String s) {
        return ItemType.findByName(s);
    }
}
