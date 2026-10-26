package com.wipfli.training.policyadmin.dto;

import java.time.LocalDate;

/**
 * Represents the data sent by the client to create a truck policy.
 * Includes customer details, policy information, truck capacity
 * details, and previous claim history used during policy processing.
 */

public class CreateTruckPolicyRequest {

    private String policyNumber;
    private String customerName;
    private int customerAge;
    private LocalDate expiryDate;
    private double loadCapacityTons;
    private int previousClaims = 0;

    public CreateTruckPolicyRequest() {
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

    public double getLoadCapacityTons() {
        return loadCapacityTons;
    }

    public void setLoadCapacityTons(double loadCapacityTons) {
        this.loadCapacityTons = loadCapacityTons;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    public void setPreviousClaims(int previousClaims) {
        this.previousClaims = previousClaims;
    }
}
