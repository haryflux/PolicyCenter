package com.wipfli.training.policyadmin.web;

import com.wipfli.training.policyadmin.dto.CreateBikePolicyRequest;
import com.wipfli.training.policyadmin.dto.CreateCarPolicyRequest;
import com.wipfli.training.policyadmin.dto.CreateTruckPolicyRequest;
import com.wipfli.training.policyadmin.dto.PolicyResponse;
import com.wipfli.training.policyadmin.dto.PremiumResponse;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.service.PolicyFactory;
import com.wipfli.training.policyadmin.service.PolicyRegister;
import com.wipfli.training.policyadmin.service.PremiumCalculable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Assignment 11: the new front door to the whole Policy Administration System.
 * This replaces the Scanner-based console menu (PremiumCalculatorApp, now
 * retired) - a client (Postman, a browser, another service) talks to this
 * class over HTTP instead of typing into a terminal.
 *
 * Nothing about the actual business rules changes here. This class just:
 *   1. turns an incoming HTTP request into a call on PolicyRegister/PolicyFactory
 *      (exactly what the console menu's handleXxx() methods used to do), and
 *   2. turns whatever comes back into an HTTP response.
 *
 * PolicyNotFoundException is checked (extends Exception, from Assignment 6),
 * so any method that can hit a missing policy declares "throws PolicyNotFoundException"
 * - Spring will hand it to GlobalExceptionHandler (added in the next step) to turn
 * it into a proper 404 response, instead of letting it crash with a generic error.
 */
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRegister policyRegister;
    private final PremiumCalculable standardCalculator;
    private final PremiumCalculable discountedCalculator;

    // Same @Qualifier idea as PolicyRegister's constructor - there are 2 beans
    // implementing PremiumCalculable, so we're explicit about which one we want
    // for each field. We want BOTH here (not just the discounted one) so the
    // /premium endpoint can show the before-and-after discount effect.
    public PolicyController(PolicyRegister policyRegister,
                            @Qualifier("standardPremiumCalculator") PremiumCalculable standardCalculator,
                            @Qualifier("noClaimBonusCalculator") PremiumCalculable discountedCalculator) {
        this.policyRegister = policyRegister;
        this.standardCalculator = standardCalculator;
        this.discountedCalculator = discountedCalculator;
    }

    /** POST /api/policies/car - creates a new car policy. */
    @PostMapping("/car")
    public ResponseEntity<PolicyResponse> createCarPolicy(@RequestBody CreateCarPolicyRequest request) {
        Customer customer = new Customer(request.getCustomerName(), request.getCustomerAge());
        Policy policy = PolicyFactory.createCarPolicy(
                request.getPolicyNumber(), customer, request.getExpiryDate(),
                request.getRegistrationNumber(), request.getPreviousClaims());

        // add() still throws DuplicatePolicyNumberException exactly like before -
        // that business rule never moved. GlobalExceptionHandler (next step) will
        // turn it into a proper 400/409 response instead of a raw stack trace.
        policyRegister.add(policy);

        return ResponseEntity.status(HttpStatus.CREATED).body(PolicyResponse.from(policy));
    }

    /** POST /api/policies/truck - creates a new truck policy. */
    @PostMapping("/truck")
    public ResponseEntity<PolicyResponse> createTruckPolicy(@RequestBody CreateTruckPolicyRequest request) {
        Customer customer = new Customer(request.getCustomerName(), request.getCustomerAge());
        // Note: TruckPolicy's own constructor still enforces "driver must be 21+"
        // (validateTruckDriverAge) - that rule lives in TruckPolicy, untouched,
        // and will surface here as InvalidPolicyDataException if violated.
        Policy policy = PolicyFactory.createTruckPolicy(
                request.getPolicyNumber(), customer, request.getExpiryDate(),
                request.getLoadCapacityTons(), request.getPreviousClaims());

        policyRegister.add(policy);

        return ResponseEntity.status(HttpStatus.CREATED).body(PolicyResponse.from(policy));
    }

    /** POST /api/policies/bike - creates a new bike policy. */
    @PostMapping("/bike")
    public ResponseEntity<PolicyResponse> createBikePolicy(@RequestBody CreateBikePolicyRequest request) {
        Customer customer = new Customer(request.getCustomerName(), request.getCustomerAge());
        Policy policy = PolicyFactory.createBikePolicy(
                request.getPolicyNumber(), customer, request.getExpiryDate(),
                request.getEngineCC(), request.getPreviousClaims());

        policyRegister.add(policy);

        return ResponseEntity.status(HttpStatus.CREATED).body(PolicyResponse.from(policy));
    }

    /** GET /api/policies/{policyNumber} - looks up one policy by number. */
    @GetMapping("/{policyNumber}")
    public ResponseEntity<PolicyResponse> getPolicy(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        return ResponseEntity.ok(PolicyResponse.from(policy));
    }

    /** GET /api/policies - lists every policy currently registered. */
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        List<PolicyResponse> responses = policyRegister.getAll().stream()
                .map(PolicyResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** POST /api/policies/{policyNumber}/claims - records a new claim on a policy. */
    @PostMapping("/{policyNumber}/claims")
    public ResponseEntity<PolicyResponse> recordClaim(@PathVariable String policyNumber) throws PolicyNotFoundException {
        // findByNumber() returns the SAME Policy object that's sitting inside
        // InMemoryPolicyRepository's map (not a copy) - so calling recordClaim()
        // directly on it mutates the stored policy in place. No re-save needed.
        Policy policy = policyRegister.findByNumber(policyNumber);
        policy.recordClaim();
        return ResponseEntity.ok(PolicyResponse.from(policy));
    }

    /** POST /api/policies/{policyNumber}/expire - marks an ACTIVE policy as EXPIRED. */
    @PostMapping("/{policyNumber}/expire")
    public ResponseEntity<PolicyResponse> expirePolicy(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        // Policy.expire() still enforces its own rule (must currently be ACTIVE) -
        // throws IllegalStatusChangeException otherwise, unchanged from Assignment 6.
        policy.expire();
        return ResponseEntity.ok(PolicyResponse.from(policy));
    }

    /**
     * POST /api/policies/{policyNumber}/renew - renews a policy.
     * Optional "asOf" query param lets you simulate a specific date (handy for
     * testing the 30-day renewal window rule); defaults to today if omitted.
     */
    @PostMapping("/{policyNumber}/renew")
    public ResponseEntity<PolicyResponse> renewPolicy(
            @PathVariable String policyNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf)
            throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        LocalDate today = (asOf != null) ? asOf : LocalDate.now();
        // Policy.renew(today) still enforces every rule exactly as Assignment 6/9
        // wrote it: must be ACTIVE, must be within 30 days of expiry, must have
        // fewer than 3 claims. Throws IllegalStatusChangeException or
        // RenewalNotAllowedException otherwise - untouched.
        policy.renew(today);
        return ResponseEntity.ok(PolicyResponse.from(policy));
    }

    /**
     * GET /api/policies/{policyNumber}/premium - calculates the premium, showing
     * both the standard figure and the no-claim-discounted figure side by side.
     */
    @GetMapping("/{policyNumber}/premium")
    public ResponseEntity<PremiumResponse> getPremium(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        double standard = standardCalculator.calculatePremium(policy);
        double discounted = discountedCalculator.calculatePremium(policy);
        return ResponseEntity.ok(new PremiumResponse(policyNumber, standard, discounted));
    }
}
