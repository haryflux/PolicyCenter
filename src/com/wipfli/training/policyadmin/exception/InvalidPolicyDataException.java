package com.wipfli.training.policyadmin.exception;

/** Thrown when a policy is given bad data (e.g. past expiry date, underage truck driver). */
public class InvalidPolicyDataException extends PolicyBusinessException {

    public InvalidPolicyDataException(String policyNumber, String message) {
        super(policyNumber, message);
    }

    /** Version that also keeps the original error (for chaining). */
    public InvalidPolicyDataException(String policyNumber, String message, Throwable cause) {
        super(policyNumber, message, cause);
    }
}