package com.wipfli.training.policyadmin.dto;

/**
 * Represents the error details returned to the client when
 * a policy-related request cannot be processed successfully.
 * Includes the policy number and a descriptive error message.
 */

public class ErrorResponse {

    private String policyNumber;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String policyNumber, String message) {
        this.policyNumber = policyNumber;
        this.message = message;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getMessage() {
        return message;
    }
}