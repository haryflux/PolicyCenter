package com.wipfli.training.policyadmin.exception;

/**
 * Thrown when we look up a policy number that isn't in the system.
 * This one extends Exception (CHECKED) on purpose - a missing policy is a
 * normal, expected situation, so the compiler forces the caller to handle it.
 */

public class PolicyNotFoundException extends Exception {

    private final String policyNumber;

    /** @param policyNumber the number that was searched for but not found */

    public PolicyNotFoundException(String policyNumber, String message) {
        super(message);
        this.policyNumber = policyNumber;
    }

    /** @return the policy number that wasn't found */

    public String getPolicyNumber() {
        return policyNumber;
    }
}