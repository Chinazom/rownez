package com.scenic.rownezcoreservice.model;

public enum OrderCategory {
    ROOM_SERVICE,
    RESTAURANT_SERVICE;

    public static OrderCategory findByName(String name) {
        OrderCategory result = null;
        for (OrderCategory direction : values()) {
            if (direction.name().equalsIgnoreCase(name)) {
                result = direction;
                break;
            }
        }
        return result;
    }
}
