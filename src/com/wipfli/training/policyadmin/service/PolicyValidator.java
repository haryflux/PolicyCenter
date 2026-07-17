package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

public class PolicyValidator {

    private static final int MINIMUM_AGE = 1;
    private static final int MAXIMUM_AGE = 100;

    public static boolean isValid(Policy policy) {
        validatePolicyNumber(policy.getPolicyNumber());
        validateCustomerName(policy.getCustomerName());
        validateAge(policy.getCustomerAge());
        validateVehicleType(policy.getVehicleType());
        validateClaims(policy.getPreviousClaims());
        return true;
    }

    public static void validatePolicyCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Number of policies must be greater than 0.");
        }
    }

    public static void validatePolicyNumber(String policyNumber) {
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy number cannot be empty.");
        }
        if (!policyNumber.matches("[A-Za-z0-9-]+")) {
            throw new IllegalArgumentException("Policy number must be alphanumeric.");
        }
    }

    public static void validateCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
    }

    public static void validateAge(int age) {
        if (age < MINIMUM_AGE || age > MAXIMUM_AGE) {
            throw new IllegalArgumentException("Age must be between 1 and 100.");
        }
    }

    public static void validateVehicleType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null.");
        }
    }

    public static void validateClaims(int claims) {
        if (claims < 0) {
            throw new IllegalArgumentException("Previous claims cannot be negative.");
        }
    }
}