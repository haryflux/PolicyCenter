package com.wipfli.training.policyadmin.model;

/**
 * Represents an insurance policy for a car.
 * In addition to the common policy details, this class stores
 * the vehicle's registration number so the car can be uniquely identified.
 */

public class CarPolicy extends Policy {

    // This is the one extra piece of info that ONLY a car policy needs.
    private final String registrationNumber;

    /**
     * Creates a car policy with no prior claim history.
     *
     * @param policyNumber       unique policy identifier
     * @param customer           policy holder
     * @param registrationNumber the car's registration number, e.g. "MP-47-5774"
     */

    public CarPolicy(String policyNumber, Customer customer, String registrationNumber) {
        super(policyNumber, customer, VehicleType.CAR);
        this.registrationNumber = registrationNumber;
    }

    /**
     * Returns the car's registration number.
     */

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /**
     * A car policy describes itself with all the shared details
     * plus its own registration number.
     */

    @Override
    public String getPolicyDetails() {
        return "Policy " + getPolicyNumber()
                + " | Customer: " + getCustomer().getName()
                + " | Status: " + getStatus()
                + " | Vehicle: " + getVehicleType()
                + " | Registration: " + registrationNumber;
    }
}