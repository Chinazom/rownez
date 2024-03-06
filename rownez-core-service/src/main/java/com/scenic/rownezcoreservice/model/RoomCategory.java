package com.scenic.rownezcoreservice.model;

public enum RoomCategory {
    SINGLE,
    DOUBLE,
    SUITE;
    public static RoomCategory findByName(String name) {
        RoomCategory result = null;
        for (RoomCategory direction : values()) {
            if (direction.name().equalsIgnoreCase(name)) {
                result = direction;
                break;
            }
        }
        return result;
    }
}
