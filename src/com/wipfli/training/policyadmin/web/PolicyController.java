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
 * Exposes REST endpoints for the Policy Administration System.
 * Handles policy creation, retrieval, lifecycle operations,
 * and premium-related requests.
 */

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRegister policyRegister;
    private final PremiumCalculable standardCalculator;
    private final PremiumCalculable discountedCalculator;

    public PolicyController(PolicyRegister policyRegister,
                            @Qualifier("standardPremiumCalculator") PremiumCalculable standardCalculator,
                            @Qualifier("noClaimBonusCalculator") PremiumCalculable discountedCalculator) {
        this.policyRegister = policyRegister;
        this.standardCalculator = standardCalculator;
        this.discountedCalculator = discountedCalculator;
    }

    @PostMapping("/car")
    public ResponseEntity<PolicyResponse> createCarPolicy(@RequestBody CreateCarPolicyRequest request) {
        Customer customer = new Customer(request.getCustomerName(), request.getCustomerAge());
        Policy policy = PolicyFactory.createCarPolicy(
                request.getPolicyNumber(), customer, request.getExpiryDate(),
                request.getRegistrationNumber(), request.getPreviousClaims());
        policyRegister.add(policy);

        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.CREATED, "Car policy created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/truck")
    public ResponseEntity<PolicyResponse> createTruckPolicy(@RequestBody CreateTruckPolicyRequest request) {
        Customer customer = new Customer(request.getCustomerName(), request.getCustomerAge());
        Policy policy = PolicyFactory.createTruckPolicy(
                request.getPolicyNumber(), customer, request.getExpiryDate(),
                request.getLoadCapacityTons(), request.getPreviousClaims());
        policyRegister.add(policy);

        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.CREATED, "Truck policy created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bike")
    public ResponseEntity<PolicyResponse> createBikePolicy(@RequestBody CreateBikePolicyRequest request) {
        Customer customer = new Customer(request.getCustomerName(), request.getCustomerAge());
        Policy policy = PolicyFactory.createBikePolicy(
                request.getPolicyNumber(), customer, request.getExpiryDate(),
                request.getEngineCC(), request.getPreviousClaims());
        policyRegister.add(policy);

        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.CREATED, "Bike policy created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{policyNumber}")
    public ResponseEntity<PolicyResponse> getPolicy(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.OK, "Policy retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {

    // Each response item includes API status information
    // to maintain a consistent response structure.

        List<PolicyResponse> responses = policyRegister.getAll().stream()
                .map(p -> PolicyResponse.from(p, HttpStatus.OK, "Policy retrieved successfully"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{policyNumber}/claims")
    public ResponseEntity<PolicyResponse> recordClaim(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        policy.recordClaim();
        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.OK, "Claim recorded successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{policyNumber}/expire")
    public ResponseEntity<PolicyResponse> expirePolicy(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        policy.expire();
        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.OK, "Policy expired successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{policyNumber}/renew")
    public ResponseEntity<PolicyResponse> renewPolicy(
            @PathVariable String policyNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf)
            throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        LocalDate today = (asOf != null) ? asOf : LocalDate.now();
        policy.renew(today);
        PolicyResponse response = PolicyResponse.from(policy, HttpStatus.OK, "Policy renewed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{policyNumber}/premium")
    public ResponseEntity<PremiumResponse> getPremium(@PathVariable String policyNumber) throws PolicyNotFoundException {
        Policy policy = policyRegister.findByNumber(policyNumber);
        double standard = standardCalculator.calculatePremium(policy);
        double discounted = discountedCalculator.calculatePremium(policy);
        return ResponseEntity.ok(new PremiumResponse(policyNumber, standard, discounted));
    }
}
