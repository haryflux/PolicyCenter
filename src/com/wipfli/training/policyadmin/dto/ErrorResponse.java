package com.wipfli.training.policyadmin.dto;

/**
 * Assignment 11: the shape of every error response GlobalExceptionHandler sends
 * back. Kept deliberately simple - just enough for a client to know WHICH
 * policy the error is about and WHY, without leaking internal Java details
 * (stack traces, class names) to whoever is calling the API.
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