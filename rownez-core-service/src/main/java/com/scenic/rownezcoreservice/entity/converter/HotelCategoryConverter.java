package com.scenic.rownezcoreservice.entity.converter;

import com.scenic.rownezcoreservice.model.RoomCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class HotelCategoryConverter  implements AttributeConverter<RoomCategory, String> {
    @Override
    public String convertToDatabaseColumn(RoomCategory category) {
        return category.name();
    }

    @Override
    public RoomCategory convertToEntityAttribute(String s) {
        return RoomCategory.findByName(s);
    }
}
