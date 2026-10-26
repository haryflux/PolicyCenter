package com.wipfli.training.policyadmin.dto;

/**
 * Holds API response status details such as status code,
 * success flag, and response message.
 */

public class APIStatus {

    private int statusCode;
    private boolean success;
    private String message;

    public APIStatus() {
    }

    public APIStatus(int statusCode, boolean success, String message) {
        this.statusCode = statusCode;
        this.success = success;
        this.message = message;
    }

    /** Creates a success status response. */

    public static APIStatus success(int statusCode, String message) {
        return new APIStatus(statusCode, true, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
