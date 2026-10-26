package com.wipfli.training.policyadmin.dto;

import java.time.LocalDate;

/**
 * Represents the data sent by the client to create a car policy.
 * Includes customer details, policy information, vehicle registration
 * details, and previous claim history used during policy processing.
 */

public class CreateCarPolicyRequest {

    private String policyNumber;
    private String customerName;
    private int customerAge;
    private LocalDate expiryDate;
    private String registrationNumber;
    private int previousClaims = 0;

    // Jackson (Spring's JSON <-> Java converter) needs a no-arg constructor
    // plus getters/setters to turn incoming JSON into this object automatically.

    public CreateCarPolicyRequest() {
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(int customerAge) {
        this.customerAge = customerAge;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    public void setPreviousClaims(int previousClaims) {
        this.previousClaims = previousClaims;
    }
}
