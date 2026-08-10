package com.wipfli.training.policyadmin.exception;

/**
 * Base type for all business rule errors.
 * Abstract - we only throw specific children, never a plain one.
 */

public abstract class PolicyBusinessException extends RuntimeException {
    private final String policyNumber;

    /** Basic error with a policy number and message. */

    public PolicyBusinessException(String policyNumber, String message) {
        super(message);
        this.policyNumber = policyNumber;
    }

    /** Same, but also keeps the original error as the cause (for chaining). */
    public PolicyBusinessException(String policyNumber, String message, Throwable cause) {
        super(message, cause);
        this.policyNumber = policyNumber;
    }

    /** @return the policy this error is about */
    public String getPolicyNumber() {
        return policyNumber;
    }
}