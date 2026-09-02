package com.wipfli.training.policyadmin.exception;

/** Thrown when a status change isn't allowed (e.g. renewing an already-expired policy). */
public class IllegalStatusChangeException extends PolicyBusinessException {

    public IllegalStatusChangeException(String policyNumber, String message) {
        super(policyNumber, message);
    }
}