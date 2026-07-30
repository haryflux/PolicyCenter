package com.wipfli.training.policyadmin.app;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.exception.PolicyBusinessException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.CarPolicy;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.TruckPolicy;
import com.wipfli.training.policyadmin.service.NoClaimBonusCalculator;
import com.wipfli.training.policyadmin.service.PolicyRegister;
import com.wipfli.training.policyadmin.service.PolicyValidator;
import com.wipfli.training.policyadmin.service.PremiumCalculable;

import java.time.LocalDate;

/**
 * A demo that tries to break every rule one by one.
 * Even when things go wrong, the program catches the error and keeps
 * running till the last line. I use a fixed date so results stay the same.
 */
final class Assignment6Demo {

    private static final PremiumCalculable premium = new NoClaimBonusCalculator();
    private static final LocalDate today = LocalDate.of(2026, 7, 25);

    static void run() {
        PolicyRegister register = new PolicyRegister();
        Policy good = null;

        // Make one good policy first so we have something to use later
        System.out.println("\n--- Creating policies ---");
        boolean ok = false;
        try {
            good = new CarPolicy("POL-1001", new Customer("Ravi", 22),
                    LocalDate.of(2026, 9, 30), "MP-47-5774", 1);
            PolicyValidator.validate(good);
            register.add(good);
            System.out.println(good.getPolicyDetails());
            System.out.println("Premium: " + premium.calculatePremium(good));
            ok = true;
        } catch (PolicyBusinessException e) {
            printRefused(e);
        } finally {
            System.out.println("AUDIT: create POL-1001 -> " + (ok ? "OK" : "FAILED"));
        }

        // Rule 1: truck driver too young (19, needs 21)
        ok = false;
        try {
            new TruckPolicy("POL-1002", new Customer("Imran", 19),
                    LocalDate.of(2026, 12, 1), 8.0);
            ok = true;
        } catch (PolicyBusinessException e) {
            printRefused(e);
        } finally {
            System.out.println("AUDIT: create POL-1002 -> " + (ok ? "OK" : "FAILED"));
        }

        System.out.println("\n--- Register ---");

        // Rule 2: adding the same policy number again
        try {
            register.add(good);
        } catch (PolicyBusinessException e) {
            printRefused(e);
        }

        // Rule 3: looking up a number that isn't there
        try {
            register.findByNumber("POL-9999");
        } catch (PolicyNotFoundException e) {
            System.out.println("REFUSED [" + e.getClass().getSimpleName() + "] "
                    + e.getPolicyNumber() + ": " + e.getMessage());
        }

        System.out.println("\n--- Renewal ---");

        // Rule 4: renewing way too early
        ok = false;
        try {
            good.renew(today);
            ok = true;
        } catch (PolicyBusinessException e) {
            printRefused(e);
        } finally {
            System.out.println("AUDIT: renew POL-1001 -> " + (ok ? "OK" : "FAILED"));
        }

        // Rule 5: too many claims (add 2 more so it becomes 3)
        ok = false;
        try {
            good.recordClaim();
            good.recordClaim();
            System.out.println("claims is now " + good.getPreviousClaims());
            good.renew(LocalDate.of(2026, 9, 15));
            ok = true;
        } catch (PolicyBusinessException e) {
            printRefused(e);
        } finally {
            System.out.println("AUDIT: renew POL-1001 -> " + (ok ? "OK" : "FAILED"));
        }

        System.out.println("\n--- Status ---");

        // Rule 6: expire it, then try to renew the expired one
        ok = false;
        try {
            good.expire();
            System.out.println("status is now " + good.getStatus());
            good.renew(LocalDate.of(2026, 9, 15));
            ok = true;
        } catch (PolicyBusinessException e) {
            printRefused(e);
        } finally {
            System.out.println("AUDIT: renew POL-1001 -> " + (ok ? "OK" : "FAILED"));
        }

        // Exception chaining: a bad number typed as text
        System.out.println("\n--- Exception chaining ---");
        try {
            parseLoad("POL-1007", "abc");
        } catch (PolicyBusinessException e) {
            printRefused(e);
            System.out.println("   caused by -> " + e.getCause());
        }

        // The whole point: everything above went wrong, but we still got here
        System.out.println("\nProgram finished normally.");
    }

    // Reads a number that came as text. If it's not a number, throw our own
    // error but keep the original one as the cause so we don't lose it.
    private static double parseLoad(String number, String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new InvalidPolicyDataException(number, "load must be a number, got '" + text + "'", e);
        }
    }

    // Small helper so all my error lines look the same
    private static void printRefused(PolicyBusinessException e) {
        System.out.println("REFUSED [" + e.getClass().getSimpleName() + "] "
                + e.getPolicyNumber() + ": " + e.getMessage());
    }
}