package com.wipfli.training.policyadmin.dto;

import java.time.LocalDate;

/**
 * Represents the data sent by the client to create a bike policy.
 * Includes customer details, policy information, engine capacity,
 * and previous claim history used during policy processing.
 */

public class CreateBikePolicyRequest {

    private String policyNumber;
    private String customerName;
    private int customerAge;
    private LocalDate expiryDate;
    private int engineCC;
    private int previousClaims = 0;

    public CreateBikePolicyRequest() {
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

    public int getEngineCC() {
        return engineCC;
    }

    public void setEngineCC(int engineCC) {
        this.engineCC = engineCC;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    public void setPreviousClaims(int previousClaims) {
        this.previousClaims = previousClaims;
    }
}
