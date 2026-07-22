package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

/**
 * Utility class containing validation logic
 * used during policy creation and input handling.
 */

public class PolicyValidator {

/**
 * Performs complete policy validation.
 * @param policy policy to validate
 * @return true if validation succeeds
 */

    public static boolean isValid(Policy policy) {
        validatePolicyNumber(policy.getPolicyNumber());
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