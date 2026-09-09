package com.wipfli.training.policyadmin.dto;

import com.wipfli.training.policyadmin.model.Policy;

import java.time.LocalDate;

/**
 * Assignment 11: what we send BACK to the client after any policy operation
 * (create, expire, renew, record claim, lookup).
 *
 * Why we don't just return the Policy object directly: Policy is our internal
 * domain model - if we returned it as-is, every internal field/method would
 * become part of our public API forever, and any future change to Policy would
 * risk silently breaking whatever client is calling us. This DTO is the
 * boundary - we control exactly what a caller can see.
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

    public PolicyResponse() {
    }

    /**
     * Builds a response DTO from a real Policy object. Kept as a static factory
     * method right here so every controller endpoint that returns a policy uses
     * the exact same mapping - one place to change if a field is ever added.
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
        response.details = policy.getPolicyDetails(); // polymorphic - each subtype's own wording
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
}
