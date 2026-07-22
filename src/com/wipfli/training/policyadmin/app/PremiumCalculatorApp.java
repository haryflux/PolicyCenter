package com.wipfli.training.policyadmin.app;

import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;
import com.wipfli.training.policyadmin.service.NoClaimBonusCalculator;
import com.wipfli.training.policyadmin.service.PolicyValidator;
import com.wipfli.training.policyadmin.service.PremiumCalculable;
import com.wipfli.training.policyadmin.service.StandardPremiumCalculator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * It is the main class of the Insurance Premium Calculator project.
 * From here, the program starts running.
 * It shows the menu and allows the user to:
 * - Add a new policy
 * - Record a claim
 * - Renew a policy
 * - Calculate premium
 * - View policy details
 * */

public class PremiumCalculatorApp {

    private static final List<Policy> policies = new ArrayList<>();
    private static final StandardPremiumCalculator standardCalculator = new StandardPremiumCalculator();
    private static final PremiumCalculable noClaimBonusCalculator = new NoClaimBonusCalculator();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        printRulesBanner();
        runMenuLoop(sc);

        sc.close();
    }

    // Menu loop

    private static void runMenuLoop(Scanner sc) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice(sc);

            switch (choice) {
                case 1 -> createPolicy(sc);
                case 2 -> recordClaimOnPolicy(sc);
                case 3 -> expirePolicy(sc);
                case 4 -> renewPolicy(sc);
                case 5 -> viewPolicyBreakdown(sc);
                case 6 -> viewAllPolicies();
                case 7 -> running = false;
                default -> System.out.println("Invalid option. Choose a number from 1 to 7.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n================ MENU ================");
        System.out.println("1. Create New Policy");
        System.out.println("2. Record Claim");
        System.out.println("3. Expire Policy");
        System.out.println("4. Renew Policy");
        System.out.println("5. View Policy Premium Breakdown");
        System.out.println("6. View All Policies");
        System.out.println("7. Exit");
        System.out.println("=======================================");
    }

    private static int readMenuChoice(Scanner sc) {
        while (true) {
            try {
                System.out.print("Choose an option: ");
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    // Menu actions

    private static void createPolicy(Scanner sc) {
        String policyNumber = readPolicyNumber(sc);
        Customer customer = readCustomer(sc);
        VehicleType vehicleType = readVehicleType(sc);
        int claims = readClaims(sc);

        Policy policy = new Policy(policyNumber, customer, vehicleType, claims);
        PolicyValidator.isValid(policy);

        policies.add(policy);
        System.out.println("Created " + policy.getPolicyNumber() + " for " + customer.getName()
                + " (age " + customer.getAge() + "), " + policy.getVehicleType()
                + ", " + policy.getPreviousClaims() + " claim(s) - status " + policy.getStatus());
    }

    private static void recordClaimOnPolicy(Scanner sc) {
        Policy policy = findPolicyByNumber(sc);
        if (policy == null) return;

        policy.recordClaim();
        System.out.println("recordClaim() called - claims is now " + policy.getPreviousClaims());
    }

    private static void expirePolicy(Scanner sc) {
        Policy policy = findPolicyByNumber(sc);
        if (policy == null) return;

        try {
            policy.expire();
            System.out.println("expire() called - status is now " + policy.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("expire() called - REJECTED: " + e.getMessage());
        }
    }

    private static void renewPolicy(Scanner sc) {
        Policy policy = findPolicyByNumber(sc);
        if (policy == null) return;

        try {
            policy.renew();
            System.out.println("renew() called - status is now " + policy.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("renew() called - REJECTED: " + e.getMessage());
        }
    }

    private static void viewPolicyBreakdown(Scanner sc) {
        Policy policy = findPolicyByNumber(sc);
        if (policy == null) return;

        printPolicyBreakdown(policy);
    }

    private static void viewAllPolicies() {
        if (policies.isEmpty()) {
            System.out.println("No policies created yet.");
            return;
        }
        for (Policy policy : policies) {
            System.out.println(policy);
        }
        printHashSetCheck();
    }

    // Lookup helper

    private static Policy findPolicyByNumber(Scanner sc) {
        System.out.print("Enter Policy Number: ");
        String input = sc.nextLine().trim();

        for (Policy policy : policies) {
            if (policy.getPolicyNumber().equals(input)) {
                return policy;
            }
        }
        System.out.println("No policy found with number: " + input);
        return null;
    }

    // Banner

    private static void printRulesBanner() {
        System.out.println("================================================");
        System.out.println("      INSURANCE PREMIUM CALCULATOR");
        System.out.println("================================================");
        System.out.println("Vehicle Base Rates: BIKE Rs.300 | CAR Rs.500 | TRUCK Rs.800");
        System.out.println("Young Driver (age < 25): +20% of base rate");
        System.out.println("Each Previous Claim: +Rs.150");
        System.out.println("No Claim Bonus: 10% off final premium if zero claims");
        System.out.println("Status rules: ACTIVE -> EXPIRED or ACTIVE -> RENEWED only");
    }

    // Input reading

    private static String readPolicyNumber(Scanner sc) {
        while (true) {
            try {
                System.out.print("Policy Number: ");
                String input = sc.nextLine().trim();
                PolicyValidator.validatePolicyNumber(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        }
    }

    private static Customer readCustomer(Scanner sc) {
        while (true) {
            try {
                String customerName = readCustomerName(sc);
                int age = readAge(sc);
                return new Customer(customerName, age);
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        }
    }

    private static String readCustomerName(Scanner sc) {
        System.out.print("Customer Name: ");
        return sc.nextLine().trim();
    }

    private static int readAge(Scanner sc) {
        while (true) {
            try {
                System.out.print("Customer Age: ");
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Validation Error: Age must be a number.");
            }
        }
    }

    private static VehicleType readVehicleType(Scanner sc) {
        while (true) {
            try {
                System.out.print("Vehicle Type (BIKE/CAR/TRUCK): ");
                String input = sc.nextLine().trim().toUpperCase();
                VehicleType vehicleType = VehicleType.valueOf(input);
                PolicyValidator.validateVehicleType(vehicleType);
                return vehicleType;
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: Invalid vehicle type. Must be BIKE, CAR, or TRUCK.");
            }
        }
    }

    private static int readClaims(Scanner sc) {
        while (true) {
            try {
                System.out.print("Number of Previous Claims: ");
                int claims = Integer.parseInt(sc.nextLine().trim());
                PolicyValidator.validateClaims(claims);
                return claims;
            } catch (NumberFormatException e) {
                System.out.println("Validation Error: Claims must be a number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        }
    }

    // Output

    private static void printPolicyBreakdown(Policy policy) {
        int baseRate = standardCalculator.getBaseRate(policy);
        double youngSurcharge = standardCalculator.getYoungDriverSurcharge(policy);
        double claimSurcharge = standardCalculator.getClaimSurcharge(policy);
        double standardPremium = standardCalculator.calculatePremium(policy);
        double finalPremium = noClaimBonusCalculator.calculatePremium(policy);
        double bonusAmount = standardPremium - finalPremium;

        System.out.println("Policy Number : " + policy.getPolicyNumber());
        System.out.println("Status        : " + policy.getStatus());
        System.out.println("  Base Premium          : Rs. " + baseRate);
        System.out.println("  Young Driver Surcharge : Rs. " + youngSurcharge);
        System.out.println("  Claim Surcharge        : Rs. " + claimSurcharge);
        System.out.println("  Standard Premium       : Rs. " + standardPremium);
        System.out.println("  No Claim Bonus         : Rs. " + bonusAmount);
        System.out.println("  Final Premium          : Rs. " + finalPremium);
    }

    private static void printHashSetCheck() {
        System.out.println("\n========== HASHSET CHECK ==========");
        Set<Policy> uniquePolicies = new HashSet<>(policies);
        System.out.println("Total Policies Entered : " + policies.size());
        System.out.println("Unique Policies in HashSet : " + uniquePolicies.size());
    }
}