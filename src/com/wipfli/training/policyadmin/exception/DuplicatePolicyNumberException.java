package com.wipfli.training.policyadmin.exception;

/** Thrown when registering a policy number that already exists. */
public class DuplicatePolicyNumberException extends PolicyBusinessException {

    public DuplicatePolicyNumberException(String policyNumber, String message) {
        super(policyNumber, message);
    }
}