package com.scenic.rownezcoreservice.model;

public enum StaffRole {
    ADMIN,
    GUEST,
    RECEPTIONIST,
    LAUNDRY,
    KITCHEN_PERSONNEL,
    BAR_MAN,
    WAITER,
    SECURITY,
    MANAGER,
    GARDENER,
    CLEANER,
    ACCOUNTANT;

    public static StaffRole findByName(String name) {
        StaffRole result = null;
        for (StaffRole staffRole : values()) {
            if (staffRole.name().equalsIgnoreCase(name)) {
                result = staffRole;
                break;
            }
        }
        return result;
    }
}
