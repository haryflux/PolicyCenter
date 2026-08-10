package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

/**
 * Validation logic for policies.
 * Throws InvalidPolicyDataException on failure instead of returning a boolean -
 * a boolean says "something's wrong", an exception says what and which policy.
 */

public class PolicyValidator {

    /** Runs all checks; throws if any fail, returns normally if valid. */

    public static void validate(Policy policy) {
        validatePolicyNumber(policy.getPolicyNumber());
        validateVehicleType(policy.getPolicyNumber(), policy.getVehicleType());
        validateClaims(policy.getPolicyNumber(), policy.getPreviousClaims());
    }

    public static void validatePolicyNumber(String policyNumber) {
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            throw new InvalidPolicyDataException("N/A", "Policy number cannot be empty.");
        }
        if (!policyNumber.matches("[A-Za-z0-9-]+")) {
            throw new InvalidPolicyDataException(policyNumber, "Policy number must be alphanumeric.");
        }
    }

    public static void validateVehicleType(String policyNumber, VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new InvalidPolicyDataException(policyNumber, "Vehicle type cannot be null.");
        }
    }

    public static void validateClaims(String policyNumber, int claims) {
        if (claims < 0) {
            throw new InvalidPolicyDataException(policyNumber, "Previous claims cannot be negative.");
        }
    }
}