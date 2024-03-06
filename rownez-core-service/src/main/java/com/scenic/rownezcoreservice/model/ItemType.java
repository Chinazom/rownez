package com.scenic.rownezcoreservice.model;

public enum ItemType {
    UNDERWEAR,
    SHIRT,
    T_SHIRT,
    JEANS,
    TROUSER,
    SKIRT,
    SUIT,
    NATIVE;

    public static ItemType findByName(String name) {
        ItemType result = null;
        for (ItemType direction : values()) {
            if (direction.name().equalsIgnoreCase(name)) {
                result = direction;
                break;
            }
        }
        return result;
    }
}
