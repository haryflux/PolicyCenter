package com.wipfli.training.policyadmin.model;

/**
 * Represents an insurance policy for a bike.
 * In addition to the standard policy information, this class
 * stores the bike's engine capacity (CC), which can be used
 * when calculating premiums or displaying vehicle details.
 */

public class BikePolicy extends Policy {

    private final int engineCC;

    /**
     * Creates a bike policy with no prior claim history (starts at 0 claims).
     */

    public BikePolicy(String policyNumber, Customer customer, int engineCC) {
        super(policyNumber, customer, VehicleType.BIKE);
        this.engineCC = engineCC;
    }

    /**
     * Creates a bike policy that already carries some claim history.
     */

    public BikePolicy(String policyNumber, Customer customer, int engineCC, int previousClaims) {
        super(policyNumber, customer, VehicleType.BIKE, previousClaims);
        this.engineCC = engineCC;
    }

    public int getEngineCC() {
        return engineCC;
    }

    @Override
    public String getPolicyDetails() {
        return baseDetails() + " | Engine: " + engineCC + " CC";
    }
}