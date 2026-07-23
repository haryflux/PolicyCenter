package com.wipfli.training.policyadmin.model;

import java.util.Objects;

/**
 * Represents an insurance policy.
 * This class owns and enforces policy business rules.
 * Status transitions and claim updates can happen only through controlled methods.
 */
public abstract class Policy {

    private final String policyNumber;
    private final Customer customer;
    private final VehicleType vehicleType;
    private int previousClaims;
    private PolicyStatus status;

    /**
     * Creates a brand-new policy that has no claim history yet.
     * Handy for the normal case where a fresh customer signs up with zero claims.
     * Simply forwards to the full constructor with previousClaims set to 0.
     * @param policyNumber unique policy identifier
     * @param customer     policy holder
     * @param vehicleType  insured vehicle type
     */

    public Policy(String policyNumber, Customer customer, VehicleType vehicleType) {
        this(policyNumber, customer, vehicleType, 0);
    }

    /**
     * Creates a new policy in ACTIVE state.
     * Policy number, customer and vehicle type are fixed after creation.
     * @param policyNumber unique policy identifier
     * @param customer policy holder
     * @param vehicleType insured vehicle type
     * @param previousClaims historical claim count already on record
     */

    public Policy(String policyNumber, Customer customer, VehicleType vehicleType, int previousClaims) {
        validatePolicyNumber(policyNumber);
        validateClaims(previousClaims);

        this.policyNumber = policyNumber;
        this.customer = customer;
        this.vehicleType = vehicleType;
        this.previousClaims = previousClaims;
        this.status = PolicyStatus.ACTIVE;
    }

    public abstract String getPolicyDetails();

    // Getters

    public String getPolicyNumber() {
        return policyNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    // Controlled mutation: status

    /**
     * Marks the policy as expired.
     * Only ACTIVE policies may be expired.
     * @throws IllegalStateException if the policy is not ACTIVE
     */


    public void expire() {
        if (!isActive()) {
            throw new IllegalStateException(
                    "Cannot expire policy " + policyNumber + ", it is " + status);
        }
        status = PolicyStatus.EXPIRED;
    }

    /**
     * Marks the policy as renewed.
     * Renewal is allowed only while the policy is ACTIVE.
     * @throws IllegalStateException if the policy is not ACTIVE
     */

    public void renew() {
        if (!isActive()) {
            throw new IllegalStateException(
                    "Cannot renew policy " + policyNumber + ", it is " + status);
        }
        status = PolicyStatus.RENEWED;
    }

    // Controlled mutation: claims

    /**
     * Records a new claim against this policy.
     * Claims can only increase one at a time through this method.
     */

    public void recordClaim() {
        previousClaims += 1;
    }

    /**
     *  Internal helper used to validate policy state transitions.
     */

    private boolean isActive() {
        return status == PolicyStatus.ACTIVE;
    }

    /**
     * Two policies are considered equal when they share
     * the same policy number.
     */

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
        return "Policy{policyNumber='" + policyNumber + "', customer=" + customer.describeForPolicy()
                + ", vehicleType=" + vehicleType + ", previousClaims=" + previousClaims
                + ", status=" + status + "}";
    }

    private static void validatePolicyNumber(String policyNumber) {
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy number cannot be blank.");
        }
    }

    private static void validateClaims(int claims) {
        if (claims < 0) {
            throw new IllegalArgumentException("Previous claims cannot be negative.");
        }
    }
}