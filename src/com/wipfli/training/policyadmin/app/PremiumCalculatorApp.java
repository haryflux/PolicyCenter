package com.wipfli.training.policyadmin.app;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.exception.PolicyBusinessException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.BikePolicy;
import com.wipfli.training.policyadmin.model.CarPolicy;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.TruckPolicy;
import com.wipfli.training.policyadmin.model.VehicleType;
import com.wipfli.training.policyadmin.service.NoClaimBonusCalculator;
import com.wipfli.training.policyadmin.service.PolicyRegister;
import com.wipfli.training.policyadmin.service.PolicyValidator;
import com.wipfli.training.policyadmin.service.PremiumCalculable;
import com.wipfli.training.policyadmin.service.StandardPremiumCalculator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Main class of the Insurance Premium Calculator.
 * Shows an interactive menu. All business failures are caught in ONE place -
 * the menu loop - so the program prints a clean message and never crashes.
 * Option 7 runs a scripted demo that proves all six rules.
 */
public class PremiumCalculatorApp {

    private static final PolicyRegister register = new PolicyRegister();
    private static final StandardPremiumCalculator standardCalculator = new StandardPremiumCalculator();
    private static final PremiumCalculable noClaimBonusCalculator = new NoClaimBonusCalculator();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        printRulesBanner();
        runMenuLoop(sc);

        sc.close();
    }

    // Menu loop - the ONE place where we catch business failures.
    private static void runMenuLoop(Scanner sc) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice(sc);

            try {
                switch (choice) {
                    case 1 -> createPolicy(sc);
                    case 2 -> recordClaimOnPolicy(sc);
                    case 3 -> expirePolicy(sc);
                    case 4 -> renewPolicy(sc);
                    case 5 -> viewPolicyBreakdown(sc);
                    case 6 -> viewAllPolicies();
                    case 7 -> Assignment6Demo.run();
                    case 8 -> running = false;
                    default -> System.out.println("Invalid option. Choose a number from 1 to 8.");
                }
            } catch (PolicyNotFoundException e) {          // checked - caught first
                printRefused(e.getClass().getSimpleName(), e.getPolicyNumber(), e.getMessage());
            } catch (PolicyBusinessException e) {          // all business rules - one catch
                printRefused(e.getClass().getSimpleName(), e.getPolicyNumber(), e.getMessage());
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
        System.out.println("7. Run Assignment 6 Demo");
        System.out.println("8. Exit");
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

    // Menu actions (they THROW; the runMenuLoop catches)

    private static void createPolicy(Scanner sc) {
        System.out.print("Policy Number: ");
        String policyNumber = sc.nextLine().trim();

        Customer customer = readCustomer(sc);
        VehicleType vehicleType = readVehicleType(sc);
        LocalDate expiryDate = readExpiryDate(sc);
        int claims = readInt(sc, "Number of Previous Claims: ");

        Policy policy;
        switch (vehicleType) {
            case CAR -> {
                System.out.print("Car Registration Number (e.g. KA-05-1234): ");
                policy = new CarPolicy(policyNumber, customer, expiryDate, sc.nextLine().trim(), claims);
            }
            case BIKE -> {
                int engineCC = readInt(sc, "Bike Engine Size (CC): ");
                policy = new BikePolicy(policyNumber, customer, expiryDate, engineCC, claims);
            }
            case TRUCK -> {
                double loadTons = readDouble(sc, "Truck Load Capacity (tons): ");
                policy = new TruckPolicy(policyNumber, customer, expiryDate, loadTons, claims);
            }
            default -> throw new InvalidPolicyDataException(policyNumber, "Unknown vehicle type.");
        }

        PolicyValidator.validate(policy);
        register.add(policy);
        System.out.println("Created " + policy.getPolicyDetails());
    }

    private static void recordClaimOnPolicy(Scanner sc) throws PolicyNotFoundException {
        Policy policy = register.findByNumber(askPolicyNumber(sc));
        policy.recordClaim();
        System.out.println("recordClaim() called - claims is now " + policy.getPreviousClaims());
    }

    private static void expirePolicy(Scanner sc) throws PolicyNotFoundException {
        Policy policy = register.findByNumber(askPolicyNumber(sc));
        policy.expire();
        System.out.println("expire() called - status is now " + policy.getStatus());
    }

    private static void renewPolicy(Scanner sc) throws PolicyNotFoundException {
        Policy policy = register.findByNumber(askPolicyNumber(sc));
        policy.renew(LocalDate.now());
        System.out.println("renew() called - status is now " + policy.getStatus());
    }

    private static void viewPolicyBreakdown(Scanner sc) throws PolicyNotFoundException {
        Policy policy = register.findByNumber(askPolicyNumber(sc));
        printPolicyBreakdown(policy);
    }

    private static void viewAllPolicies() {
        if (register.getAll().isEmpty()) {
            System.out.println("No policies created yet.");
            return;
        }
        for (Policy policy : register.getAll()) {
            System.out.println(policy);
        }
    }

    // Lookup helper

    private static String askPolicyNumber(Scanner sc) {
        System.out.print("Enter Policy Number: ");
        return sc.nextLine().trim();
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

    private static Customer readCustomer(Scanner sc) {
        System.out.print("Customer Name: ");
        String name = sc.nextLine().trim();
        int age = readInt(sc, "Customer Age: ");
        return new Customer(name, age);          // may throw InvalidPolicyDataException
    }

    private static VehicleType readVehicleType(Scanner sc) {
        while (true) {
            System.out.print("Vehicle Type (BIKE/CAR/TRUCK): ");
            String input = sc.nextLine().trim().toUpperCase();
            try {
                return VehicleType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Must be BIKE, CAR, or TRUCK.");
            }
        }
    }

    private static LocalDate readExpiryDate(Scanner sc) {
        while (true) {
            System.out.print("Expiry Date (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Please use the format YYYY-MM-DD, e.g. 2026-09-30.");
            }
        }
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
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
        System.out.println("  Base Premium           : Rs. " + baseRate);
        System.out.println("  Young Driver Surcharge : Rs. " + youngSurcharge);
        System.out.println("  Claim Surcharge        : Rs. " + claimSurcharge);
        System.out.println("  Standard Premium       : Rs. " + standardPremium);
        System.out.println("  No Claim Bonus         : Rs. " + bonusAmount);
        System.out.println("  Final Premium          : Rs. " + finalPremium);
    }

    private static void printRefused(String type, String policyNumber, String message) {
        System.out.println("REFUSED [" + type + "] " + policyNumber + ": " + message);
    }
}