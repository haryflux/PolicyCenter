package com.wipfli.training.policyadmin.model;

public enum VehicleType {
    BIKE(300),
    CAR(500),
    TRUCK(800);

    private final int baseRate;

    VehicleType(int baseRate) {
        this.baseRate = baseRate;
    }

    public int getBaseRate() {
        return baseRate;
    }
}