package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.exception.DuplicatePolicyNumberException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.Policy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores all policies by their policy number.
 * Rejects duplicate numbers, and never returns null on a lookup.
 */

public class PolicyRegister {

    private final Map<String, Policy> policies = new HashMap<>();

    /**
     * Adds a policy.
     * @throws DuplicatePolicyNumberException if the number is already registered
     */

    public void add(Policy policy) {
        String number = policy.getPolicyNumber();
        if (policies.containsKey(number)) {
            throw new DuplicatePolicyNumberException(number,
                    "a policy with this number is already registered");
        }
        policies.put(number, policy);
    }

    /**
     * Finds a policy by its number - never returns null.
     * @throws PolicyNotFoundException if no policy has that number
     */

    public Policy findByNumber(String policyNumber) throws PolicyNotFoundException {
        Policy policy = policies.get(policyNumber);
        if (policy == null) {
            throw new PolicyNotFoundException(policyNumber, "no policy with this number");
        }
        return policy;
    }

    /** Returns all policies, so the menu can list them. */
    public Collection<Policy> getAll() {
        return policies.values();
    }
}