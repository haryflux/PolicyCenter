package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.BikePolicy;
import com.wipfli.training.policyadmin.model.CarPolicy;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.TruckPolicy;

import java.time.LocalDate;

/**
 * Assignment 10 (Factory): Centralizing all policy creation here.
 * This is the only place allowed to instantiate concrete policy subtypes,
 * so we use this factory instead of manually spinning up a 'new CarPolicy(...)'.
 */

public class PolicyFactory {

    // Utility class - not meant to be instantiated.
    private PolicyFactory() {
    }

    /** Builds a car policy. */
    public static Policy createCarPolicy(String policyNumber, Customer customer,
                                         LocalDate expiryDate, String registrationNumber, int claims) {
        return new CarPolicy(policyNumber, customer, expiryDate, registrationNumber, claims);
    }

    /** Builds a truck policy. */
    public static Policy createTruckPolicy(String policyNumber, Customer customer,
                                           LocalDate expiryDate, double loadCapacityTons, int claims) {
        return new TruckPolicy(policyNumber, customer, expiryDate, loadCapacityTons, claims);
    }

    /** Builds a bike policy. */
    public static Policy createBikePolicy(String policyNumber, Customer customer,
                                          LocalDate expiryDate, int engineCC, int claims) {
        return new BikePolicy(policyNumber, customer, expiryDate, engineCC, claims);
    }
}