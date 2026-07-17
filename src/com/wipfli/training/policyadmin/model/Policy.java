package com.wipfli.training.policyadmin.model;

import java.util.Objects;

public class Policy {

    private final String policyNumber;
    private final String customerName;
    private final int customerAge;
    private final VehicleType vehicleType;
    private final int previousClaims;

    public Policy(String policyNumber, String customerName, int customerAge,
                  VehicleType vehicleType, int previousClaims) {
        this.policyNumber = policyNumber;
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.vehicleType = vehicleType;
        this.previousClaims = previousClaims;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Policy)) return false;
        Policy other = (Policy) o;
        return Objects.equals(policyNumber, other.policyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyNumber);
    }

    @Override
    public String toString() {
        return "Policy{" +
                "policyNumber='" + policyNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerAge=" + customerAge +
                ", vehicleType=" + vehicleType +
                ", previousClaims=" + previousClaims +
                '}';
    }
}