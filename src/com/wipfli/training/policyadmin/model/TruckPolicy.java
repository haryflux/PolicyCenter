package com.wipfli.training.policyadmin.model;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import java.time.LocalDate;
/**
 * Represents a truck insurance policy.
 * In addition to the common policy details, it keeps track
 * of the truck's load capacity (in tons), which may be used
 * when calculating premiums or displaying vehicle information.
 */

public class TruckPolicy extends Policy {

    private static final int MINIMUM_TRUCK_DRIVER_AGE = 21;
    private final double loadCapacityTons;

    /**
     * Creates a truck policy with no prior claim history (starts at 0 claims).
     */

    public TruckPolicy(String policyNumber, Customer customer, LocalDate expiryDate, double loadCapacityTons) {
        super(policyNumber, customer, VehicleType.TRUCK, expiryDate);
        this.loadCapacityTons = loadCapacityTons;
    }

    /**
     * Creates a truck policy that already carries some claim history.
     */

    public TruckPolicy(String policyNumber, Customer customer, LocalDate expiryDate, double loadCapacityTons, int previousClaims) {
        super(policyNumber, customer, VehicleType.TRUCK, expiryDate, previousClaims);
        this.loadCapacityTons = loadCapacityTons;
    }

    public double getLoadCapacityTons() {
        return loadCapacityTons;
    }

    @Override
    public String getPolicyDetails() {
        return baseDetails() + " | Load capacity: " + loadCapacityTons + " tons";
    }

    /** A truck driver must be at least 21.*/

    private static void validateTruckDriverAge(String policyNumber, Customer customer) {
        if (customer.getAge() < MINIMUM_TRUCK_DRIVER_AGE) {
            throw new InvalidPolicyDataException(policyNumber,
                    "a truck policy needs a driver of at least 21, this driver is " + customer.getAge());
        }
    }
}