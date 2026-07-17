package com.wipfli.training.policyadmin.app;

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

public class PremiumCalculatorApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        printRulesBanner();

        int count = readPolicyCount(sc);
        List<Policy> policies = readPolicies(sc, count);

        StandardPremiumCalculator standardCalculator = new StandardPremiumCalculator();
        PremiumCalculable noClaimBonusCalculator = new NoClaimBonusCalculator();

        printPremiumSummary(policies, standardCalculator, noClaimBonusCalculator);
        printHashSetCheck(policies);

        sc.close();
    }

    private static void printRulesBanner() {
        System.out.println("================================================");
        System.out.println("      INSURANCE PREMIUM CALCULATOR");
        System.out.println("================================================");
        System.out.println("\nVehicle Base Rates: BIKE Rs.300 | CAR Rs.500 | TRUCK Rs.800");
        System.out.println("Young Driver (age < 25): +20% of base rate");
        System.out.println("Each Previous Claim: +Rs.150");
        System.out.println("No Claim Bonus: 10% off final premium if zero claims");
    }

    // ---------- Input with retry-on-invalid ----------

    private static int readPolicyCount(Scanner sc) {
        while (true) {
            try {
                System.out.print("\nEnter Number of Policies: ");
                int count = Integer.parseInt(sc.nextLine().trim());
                PolicyValidator.validatePolicyCount(count);
                return count;
            } catch (NumberFormatException e) {
                System.out.println("Validation Error: Please enter a number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        }
    }

    private static List<Policy> readPolicies(Scanner sc, int count) {
        List<Policy> policies = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            System.out.println("\n---- Policy " + i + " ----");
            policies.add(readSinglePolicy(sc));
        }
        return policies;
    }

    private static Policy readSinglePolicy(Scanner sc) {
        String policyNumber = readPolicyNumber(sc);
        String customerName = readCustomerName(sc);
        int age = readAge(sc);
        VehicleType vehicleType = readVehicleType(sc);
        int claims = readClaims(sc);
        return new Policy(policyNumber, customerName, age, vehicleType, claims);
    }

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

    private static String readCustomerName(Scanner sc) {
        while (true) {
            try {
                System.out.print("Customer Name: ");
                String input = sc.nextLine().trim();
                PolicyValidator.validateCustomerName(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        }
    }

    private static int readAge(Scanner sc) {
        while (true) {
            try {
                System.out.print("Customer Age: ");
                int age = Integer.parseInt(sc.nextLine().trim());
                PolicyValidator.validateAge(age);
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Validation Error: Age must be a number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
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


    private static void printPremiumSummary(List<Policy> policies,
                                            StandardPremiumCalculator standardCalculator,
                                            PremiumCalculable noClaimBonusCalculator) {
        System.out.println("\n================================================");
        System.out.println("             PREMIUM SUMMARY");
        System.out.println("================================================");

        for (Policy policy : policies) {
            printPolicyBreakdown(policy, standardCalculator, noClaimBonusCalculator);
        }
    }

    private static void printPolicyBreakdown(Policy policy,
                                             StandardPremiumCalculator standardCalculator,
                                             PremiumCalculable noClaimBonusCalculator) {
        int baseRate = standardCalculator.getBaseRate(policy);
        double youngSurcharge = standardCalculator.getYoungDriverSurcharge(policy);
        double claimSurcharge = standardCalculator.getClaimSurcharge(policy);
        double standardPremium = standardCalculator.calculatePremium(policy);
        double finalPremium = noClaimBonusCalculator.calculatePremium(policy);
        double bonusAmount = standardPremium - finalPremium;

        System.out.println("\nPolicy Number : " + policy.getPolicyNumber());
        System.out.println("Customer Name : " + policy.getCustomerName());
        System.out.println("  Base Premium          : Rs. " + baseRate);
        System.out.println("  Young Driver Surcharge : Rs. " + youngSurcharge);
        System.out.println("  Claim Surcharge        : Rs. " + claimSurcharge);
        System.out.println("  Standard Premium       : Rs. " + standardPremium);
        System.out.println("  No Claim Bonus         : Rs. " + bonusAmount);
        System.out.println("  Final Premium          : Rs. " + finalPremium);
        System.out.println("--------------------------------");
    }

    private static void printHashSetCheck(List<Policy> policies) {
        System.out.println("\n========== HASHSET CHECK ==========");

        Set<Policy> uniquePolicies = new HashSet<>(policies);

        System.out.println("Total Policies Entered : " + policies.size());
        System.out.println("Unique Policies in HashSet : " + uniquePolicies.size());
    }
}