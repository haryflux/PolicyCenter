package com.wipfli.training.policyadmin.model;

/**
 * Represents a bike insurance policy.
 * Along with the common policy details, it keeps track of the bike's engine size in CC.
 */

public class BikePolicy extends Policy {

    // This is the one extra piece of info that ONLY a bike policy needs.
    private final int engineCC;

    /**
     * Creates a bike policy with no prior claim history.
     *
     * @param policyNumber unique policy identifier
     * @param customer     policy holder
     * @param engineCC     the bike's engine size in cc, e.g. 150
     */

    public BikePolicy(String policyNumber, Customer customer, int engineCC) {
        super(policyNumber, customer, VehicleType.BIKE);
        this.engineCC = engineCC;
    }

    /**
     * Returns the bike's engine size in cc.
     */

    public int getEngineCC() {
        return engineCC;
    }

    /**
     * A bike policy describes itself with all the shared details
     * plus its own engine size.
     */

    @Override
    public String getPolicyDetails() {
        return "Policy " + getPolicyNumber()
                + " | Customer: " + getCustomer().getName()
                + " | Status: " + getStatus()
                + " | Vehicle: " + getVehicleType()
                + " | Engine: " + engineCC + " CC";
    }
}