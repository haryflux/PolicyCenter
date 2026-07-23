package com.wipfli.training.policyadmin.model;

/**
 * Represents a truck insurance policy.
 * In addition to the common policy details, it keeps track
 * of the truck's load capacity (in tons), which may be used
 * when calculating premiums or displaying vehicle information.
 */

public class TruckPolicy extends Policy {

    // This is the one extra piece of info that ONLY a truck policy needs.
    private final double loadCapacityTons;

    /**
     * Creates a truck policy with no prior claim history.
     *
     * @param policyNumber     unique policy identifier
     * @param customer         policy holder
     * @param loadCapacityTons the truck's load capacity in tons, e.g. 8.0
     */

    public TruckPolicy(String policyNumber, Customer customer, double loadCapacityTons) {
        super(policyNumber, customer, VehicleType.TRUCK);
        this.loadCapacityTons = loadCapacityTons;
    }

    /**
     * Returns the truck's load capacity in tons.
     */

    public double getLoadCapacityTons() {
        return loadCapacityTons;
    }

    /**
     * A truck policy describes itself with all the shared details
     * plus its own load capacity.
     */

    @Override
    public String getPolicyDetails() {
        return "Policy " + getPolicyNumber()
                + " | Customer: " + getCustomer().getName()
                + " | Status: " + getStatus()
                + " | Vehicle: " + getVehicleType()
                + " | Load capacity: " + loadCapacityTons + " tons";
    }
}