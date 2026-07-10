import java.util.Scanner;

public class PremiumCalculator {

    enum VehicleType {
        BIKE(1, 300),
        CAR(2, 500),
        TRUCK(3, 800);

        private final int id;
        private final int baseRate;

        VehicleType(int id, int baseRate) {
            this.id = id;
            this.baseRate = baseRate;
        }

        public int getId() {
            return id;
        }

        public int getBaseRate() {
            return baseRate;
        }

        public static VehicleType fromId(int id) {
            for (VehicleType type : VehicleType.values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid vehicle ID");
        }
    }

    private static final int CRASH_SURCHARGE = 150;
    private static final double YOUNG_DRIVER_SURCHARGE_PERCENT = 0.20;
    private static final int YOUNG_DRIVER_AGE_LIMIT = 25;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            int age = readAge(sc);
            int choice = readVehicleChoice(sc);
            int crashes = readCrashes(sc);

            double premium = calculatePremium(age, choice, crashes);

            printPremiumDetails(age, choice, crashes, premium);

        } catch (IllegalArgumentException e) {
            System.out.println("\nValidation Error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    private static int readAge(Scanner sc) {
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        validateAge(age);
        return age;
    }

    private static int readVehicleChoice(Scanner sc) {
        System.out.print("Enter Vehicle (1 for Bike, 2 for Car, 3 for Truck): ");
        int choice = sc.nextInt();
        validateVehicleChoice(choice);
        return choice;
    }

    private static int readCrashes(Scanner sc) {
        System.out.print("Enter Number of Past Crashes: ");
        int crashes = sc.nextInt();
        validateCrashes(crashes);
        return crashes;
    }

    private static void validateAge(int age) {
        if (age <= 0 || age > 100) {
            throw new IllegalArgumentException("Invalid age. Must be between 1 and 100.");
        }
    }

    private static void validateVehicleChoice(int choice) {
        try {
            VehicleType.fromId(choice);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vehicle choice. Please select 1, 2, or 3.");
        }
    }

    private static void validateCrashes(int crashes) {
        if (crashes < 0) {
            throw new IllegalArgumentException("Crashes cannot be negative.");
        }
    }


    private static double calculatePremium(int age, int choice, int crashes) {
        int baseRate = findBaseRate(choice);
        double premium = baseRate;

        if (isYoungDriver(age)) {
            premium += youngDriverSurcharge(baseRate);
        }

        premium += crashSurcharge(crashes);

        return premium;
    }

    private static int findBaseRate(int choice) {
        return VehicleType.fromId(choice).getBaseRate();
    }

    private static boolean isYoungDriver(int age) {
        return age < YOUNG_DRIVER_AGE_LIMIT;
    }

    private static double youngDriverSurcharge(int baseRate) {
        return baseRate * YOUNG_DRIVER_SURCHARGE_PERCENT;
    }

    private static double crashSurcharge(int crashes) {
        return crashes * CRASH_SURCHARGE;
    }

    private static void printPremiumDetails(int age, int choice, int crashes, double premium) {
        VehicleType vehicle = VehicleType.fromId(choice);
        int baseRate = vehicle.getBaseRate();
        double youngCharge = isYoungDriver(age) ? youngDriverSurcharge(baseRate) : 0;
        double crashCharge = crashSurcharge(crashes);

        System.out.println("\n---- Insurance Premium Details ----");
        System.out.println("Age: " + age);
        System.out.println("Vehicle: " + vehicle.name());
        System.out.println("Past Crashes: " + crashes);
        System.out.println("Base Premium: " + baseRate);
        System.out.println("Young Driver Surcharge: " + youngCharge);
        System.out.println("Crash Surcharge: " + crashCharge);
        System.out.println("Final Premium: " + premium);
        System.out.println("------------------------------------");
    }
}