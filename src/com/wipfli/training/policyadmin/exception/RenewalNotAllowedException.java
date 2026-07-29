package com.wipfli.training.policyadmin.exception;

/** Thrown when a renewal is refused (too early, or too many claims). */
public class RenewalNotAllowedException extends PolicyBusinessException {

    public RenewalNotAllowedException(String policyNumber, String message) {
        super(policyNumber, message);
    }
}