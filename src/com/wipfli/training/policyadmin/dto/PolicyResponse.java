package com.wipfli.training.policyadmin.dto;

import com.wipfli.training.policyadmin.model.Policy;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

/**
 * Represents the policy details returned to the client after
 * a policy operation. Includes policy information, customer
 * details, claim history, and API response status information.
 */

public class PolicyResponse {

    private String policyNumber;
    private String vehicleType;
    private String status;
    private String customerName;
    private int customerAge;
    private LocalDate expiryDate;
    private int previousClaims;
    private String details;
    private APIStatus apiStatus;

    public PolicyResponse() {
    }

    /**
     * Creates a response object from the given policy.
     * This method maps only policy-related fields and does not
     * include API status information.
     */

    public static PolicyResponse from(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        response.policyNumber = policy.getPolicyNumber();
        response.vehicleType = policy.getVehicleType().name();
        response.status = policy.getStatus().name();
        response.customerName = policy.getCustomer().getName();
        response.customerAge = policy.getCustomer().getAge();
        response.expiryDate = policy.getExpiryDate();
        response.previousClaims = policy.getPreviousClaims();
        response.details = policy.getPolicyDetails();
        return response;
    }

    /**
     * Creates a response object with policy details and
     * API status information. Useful for controller responses
     * where both business data and request outcome need to be returned.
     */

    public static PolicyResponse from(Policy policy, HttpStatus httpStatus, String message) {
        PolicyResponse response = from(policy);
        response.apiStatus = APIStatus.success(httpStatus.value(), message);
        return response;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    public String getDetails() {
        return details;
    }

    public APIStatus getApiStatus() {
        return apiStatus;
    }
}
