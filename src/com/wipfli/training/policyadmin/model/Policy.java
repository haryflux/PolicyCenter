package com.wipfli.training.policyadmin.model;

import com.wipfli.training.policyadmin.exception.IllegalStatusChangeException;
import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.exception.RenewalNotAllowedException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents an insurance policy.
 * Owns and enforces policy business rules through controlled methods.
 */
public abstract class Policy {

    private final String policyNumber;
    private final Customer customer;
    private final VehicleType vehicleType;
    private final LocalDate expiryDate;
    private int previousClaims;
    private PolicyStatus status;

    /** Creates a policy with no claim history (starts at 0). */
    public Policy(String policyNumber, Customer customer, VehicleType vehicleType, LocalDate expiryDate) {
        this(policyNumber, customer, vehicleType, expiryDate, 0);
    }

    /** Creates a policy in ACTIVE state, possibly with some claim history. */
    public Policy(String policyNumber, Customer customer, VehicleType vehicleType,
                  LocalDate expiryDate, int previousClaims) {
        validatePolicyNumber(policyNumber);
        validateExpiryDate(policyNumber, expiryDate);
        validateClaims(policyNumber, previousClaims);

        this.policyNumber = policyNumber;
        this.customer = customer;
        this.vehicleType = vehicleType;
        this.expiryDate = expiryDate;
        this.previousClaims = previousClaims;
        this.status = PolicyStatus.ACTIVE;
    }

    public abstract String getPolicyDetails();

    /** The shared part of the description; subclasses add their own bit. */
    public String baseDetails() {
        return "Policy " + getPolicyNumber()
                + " | Customer: " + getCustomer().getName()
                + " | Status: " + getStatus()
                + " | Vehicle: " + getVehicleType();
    }

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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getPreviousClaims() {
        return previousClaims;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    // policy status can change only through business methods like renew(), expire() etc.

    /** Marks the policy as expired. Only an ACTIVE policy can expire. */
    public void expire() {
        if (!isActive()) {
            throw new IllegalStatusChangeException(policyNumber,
                    "cannot expire a policy that is " + status);
        }
        status = PolicyStatus.EXPIRED;
    }
    /**
     * Renews the policy if all rules pass: must be ACTIVE, must be within the
     * last 30 days before expiry, and must have fewer than 3 claims.
     * The date is passed in (not LocalDate.now()) so it can be tested.
     *
     * @param today the date renewal is being attempted
     */
    public void renew(LocalDate today) {
        // Should be active atleast
        if (!isActive()) {
            throw new IllegalStatusChangeException(policyNumber,
                    "cannot renew a policy that is " + status);
        }

        // renewal only opens in the last 30 days before expiry
        LocalDate renewalOpens = expiryDate.minusDays(30);
        if (today.isBefore(renewalOpens)) {
            long daysEarly = ChronoUnit.DAYS.between(today, renewalOpens);
            throw new RenewalNotAllowedException(policyNumber,
                    "renewal opens on " + renewalOpens + ", that is " + daysEarly + " days away");
        }

        // 3 or more claims must go to underwriting
        if (previousClaims >= 3) {
            throw new RenewalNotAllowedException(policyNumber,
                    previousClaims + " claims on record, 3 or more must be referred to underwriting");
        }

        status = PolicyStatus.RENEWED;
    }

    // Claims can be updated only through dedicated methods that validate business rules.

    /** Records a new claim (increases the count by one). */
    public void recordClaim() {
        previousClaims += 1;
    }

    /** Internal helper: is the policy currently ACTIVE? */
    private boolean isActive() {
        return status == PolicyStatus.ACTIVE;
    }

    /** Two policies are equal when they share the same policy number. */
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
                + ", vehicleType=" + vehicleType + ", expiryDate=" + expiryDate
                + ", previousClaims=" + previousClaims + ", status=" + status + "}";
    }

    private static void validatePolicyNumber(String policyNumber) {
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            throw new InvalidPolicyDataException(policyNumber, "Policy number cannot be blank.");
        }
    }

    /** A policy must have an expiry date, and it can't be in the past. */
    private static void validateExpiryDate(String policyNumber, LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new InvalidPolicyDataException(policyNumber, "Expiry date is required.");
        }
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new InvalidPolicyDataException(policyNumber,
                    "Expiry date " + expiryDate + " is in the past.");
        }
    }

    private static void validateClaims(String policyNumber, int claims) {
        if (claims < 0) {
            throw new InvalidPolicyDataException(policyNumber, "Previous claims cannot be negative.");
        }
    }
}