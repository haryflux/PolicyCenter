package com.wipfli.training.policyadmin.model;

/**
 * Represents a truck insurance policy.
 * In addition to the common policy details, it keeps track
 * of the truck's load capacity (in tons), which may be used
 * when calculating premiums or displaying vehicle information.
 */

public class TruckPolicy extends Policy {

    private final double loadCapacityTons;

    /**
     * Creates a truck policy with no prior claim history (starts at 0 claims).
     */

    public TruckPolicy(String policyNumber, Customer customer, double loadCapacityTons) {
        super(policyNumber, customer, VehicleType.TRUCK);
        this.loadCapacityTons = loadCapacityTons;
    }

    /**
     * Creates a truck policy that already carries some claim history.
     */

    public TruckPolicy(String policyNumber, Customer customer, double loadCapacityTons, int previousClaims) {
        super(policyNumber, customer, VehicleType.TRUCK, previousClaims);
        this.loadCapacityTons = loadCapacityTons;
    }

    public double getLoadCapacityTons() {
        return loadCapacityTons;
    }

    @Override
    public String getPolicyDetails() {
        return "Policy " + getPolicyNumber()
                + " | Customer: " + getCustomer().getName()
                + " | Status: " + getStatus()
                + " | Vehicle: " + getVehicleType()
                + " | Load capacity: " + loadCapacityTons + " tons";
    }
}