package com.wipfli.training.policyadmin.dto;

import java.time.LocalDate;

/**
 * Assignment 11: what a client sends us (as JSON) to create a new car policy.
 * Note: your PolicyFactory/Customer design never stored customers separately -
 * a Customer is always built inline, right when a Policy is created. So instead
 * of a separate "create policyholder first" step, this request just carries the
 * customer's name and age alongside the policy details, matching how your code
 * actually works.
 */
public class CreateCarPolicyRequest {

    private String policyNumber;
    private String customerName;
    private int customerAge;
    private LocalDate expiryDate;
    private String registrationNumber;
    private int previousClaims = 0; // defaults to 0, same as a brand new policy with no claim history

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
