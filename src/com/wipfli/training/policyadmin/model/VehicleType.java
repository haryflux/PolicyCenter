package com.wipfli.training.policyadmin.model;

/**
 * Supported vehicle categories and their base premium rates.
 */

public enum VehicleType {

    BIKE(300),
    CAR(500),
    TRUCK(800);

    private final int baseRate;

    VehicleType(int baseRate) {
        this.baseRate = baseRate;
    }

    /**
     * Returns the standard base premium rate for this vehicle type.
     * @return base premium rate
     */
    public int getBaseRate() {
        return baseRate;
    }
}