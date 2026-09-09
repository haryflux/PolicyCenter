package com.wipfli.training.policyadmin.dto;

import java.time.LocalDate;

/**
 * Assignment 11: what a client sends us (as JSON) to create a new bike policy.
 * Same customer-inline approach as CreateCarPolicyRequest - see that file's
 * comment for why.
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
