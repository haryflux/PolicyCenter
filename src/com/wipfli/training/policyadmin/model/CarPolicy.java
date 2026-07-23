package com.wipfli.training.policyadmin.model;

/**
 * Represents an insurance policy for a car.
 * In addition to the common policy details, this class stores
 * the vehicle's registration number so the car can be uniquely identified.
 */

public class CarPolicy extends Policy {

    private final String registrationNumber;

    /**
     * Creates a car policy with no prior claim history (starts at 0 claims).
     */

    public CarPolicy(String policyNumber, Customer customer, String registrationNumber) {
        super(policyNumber, customer, VehicleType.CAR);           // parent's 3-arg constructor
        this.registrationNumber = registrationNumber;
    }

    /**
     * Creates a car policy that already carries some claim history.
     */

    public CarPolicy(String policyNumber, Customer customer, String registrationNumber, int previousClaims) {
        super(policyNumber, customer, VehicleType.CAR, previousClaims);  // parent's 4-arg constructor
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public String getPolicyDetails() {
        return "Policy " + getPolicyNumber()
                + " | Customer: " + getCustomer().getName()
                + " | Status: " + getStatus()
                + " | Vehicle: " + getVehicleType()
                + " | Registration: " + registrationNumber;
    }
}
