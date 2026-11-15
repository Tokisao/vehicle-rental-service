package com.rental;

import com.rental.facade.RentalFacade;
import com.rental.factory.VehicleFactory;
import com.rental.model.Customer;
import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;
import com.rental.strategy.*;
import java.util.*;

public class RentalApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Vehicle> availableVehicles = new LinkedHashMap<>();
    private static final List<RentalAgreement> rentalHistory = new ArrayList<>();
    private static final RentalFacade facade = new RentalFacade();
    private static int agreementCounter = 1;

    public static void main(String[] args) {
        // Pre-populate the vehicle fleet
        availableVehicles.put("CAR-001", VehicleFactory.createVehicle("Car", "CAR-001"));
        availableVehicles.put("CAR-002", VehicleFactory.createVehicle("Car", "CAR-002"));
        availableVehicles.put("BIKE-001", VehicleFactory.createVehicle("Bike", "BIKE-001"));
        availableVehicles.put("SCOOTER-001", VehicleFactory.createVehicle("ElectricScooter", "SCOOTER-001"));

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      WELCOME TO THE VEHICLE RENTAL SYSTEM     ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> showAvailableVehicles();
                case 2 -> rentVehicle();
                case 3 -> returnVehicle();
                case 4 -> showRentalHistory();
                case 5 -> calculatePricePreview();
                case 6 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n┌──────────────── MENU ────────────────┐");
        System.out.println("│ 1. Show available vehicles            │");
        System.out.println("│ 2. Create a rental                    │");
        System.out.println("│ 3. Return a vehicle                   │");
        System.out.println("│ 4. Rental history                     │");
        System.out.println("│ 5. Price calculator                   │");
        System.out.println("│ 6. Exit                               │");
        System.out.println("└───────────────────────────────────────┘");
    }

    // ─── 1. Show available vehicles ──────────────────────────────────
    private static void showAvailableVehicles() {
        System.out.println("\n── Available Vehicles ──");
        boolean any = false;
        for (Vehicle v : availableVehicles.values()) {
            if (v.isAvailable()) {
                System.out.printf("  [+] %-10s | %-12s | $%.2f/hour%n",
                    v.getId(), v.getType(), v.getBasePricePerHour());
                any = true;
            }
        }
        if (!any) System.out.println("  (no vehicles available)");
    }

    // ─── 2. Create a rental ──────────────────────────────────────────
    private static void rentVehicle() {
        System.out.println("\n── Create a Rental ──");
        showAvailableVehicles();

        System.out.print("Enter vehicle ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        Vehicle vehicle = availableVehicles.get(id);
        if (vehicle == null) {
            System.out.println("No vehicle found with that ID.");
            return;
        }
        if (!vehicle.isAvailable()) {
            System.out.println("This vehicle is already rented.");
            return;
        }

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        Customer customer = new Customer(name);

        int hours = readInt("Enter rental hours: ");
        if (hours <= 0) {
            System.out.println("Hours must be positive.");
            return;
        }

        // Select pricing strategy (label only — facade resolves the strategy)
        System.out.println("\nChoose a pricing strategy:");
        System.out.println("  1. Hourly");
        System.out.println("  2. Daily (20% discount)");
        System.out.println("  3. Weekend (+15% fee)");
        int stratChoice = readInt("Your choice: ");
        String pricingType = switch (stratChoice) {
            case 1 -> "hourly";
            case 2 -> "daily";
            case 3 -> "weekend";
            default -> {
                System.out.println("Invalid choice, using hourly pricing.");
                yield "hourly";
            }
        };

        // Collect extras as labels (facade passes them to the builder)
        List<String> extras = new ArrayList<>();
        System.out.println("\nExtras (enter numbers separated by commas, or 0 to skip):");
        System.out.println("  1. GPS (+$5)");
        System.out.println("  2. Child Seat (+$3)");
        System.out.println("  3. Full Insurance (+$15)");
        System.out.print("Your choice: ");
        String extrasInput = scanner.nextLine().trim();

        if (!extrasInput.equals("0") && !extrasInput.isEmpty()) {
            for (String p : extrasInput.split(",")) {
                switch (p.trim()) {
                    case "1" -> extras.add("GPS");
                    case "2" -> extras.add("Child Seat");
                    case "3" -> extras.add("Full Insurance");
                }
            }
        }

        // Delegate agreement creation to the facade
        try {
            RentalAgreement agreement = facade.createAgreement(
                    customer, vehicle, hours, pricingType,
                    extras.toArray(new String[0])
            );

            System.out.println("\n── Rental Agreement #" + (agreementCounter++) + " ──");
            System.out.println(agreement);

            System.out.print("\nPay and rent now? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y") || confirm.equals("yes")) {
                facade.payAndRent(agreement);   // facade handles setAvailable + notify
                rentalHistory.add(agreement);
                System.out.println("Rental completed!");
            } else {
                System.out.println("Rental cancelled.");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── 3. Return a vehicle ─────────────────────────────────────────
    private static void returnVehicle() {
        System.out.println("\n── Return a Vehicle ──");
        List<RentalAgreement> active = rentalHistory.stream()
                .filter(a -> !a.getVehicle().isAvailable())
                .toList();

        if (active.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }

        for (int i = 0; i < active.size(); i++) {
            RentalAgreement a = active.get(i);
            System.out.printf("  %d. %s | %s | %d h | $%.2f%n",
                    i + 1, a.getVehicle().getId(), a.getVehicle().getType(),
                    a.getHours(), a.getTotalCost());
        }

        int idx = readInt("Select rental number to return (0 to cancel): ");
        if (idx <= 0 || idx > active.size()) {
            System.out.println("Cancelled.");
            return;
        }

        RentalAgreement agreement = active.get(idx - 1);
        facade.returnVehicle(agreement);   // facade handles setAvailable + notify
        System.out.println("Vehicle " + agreement.getVehicle().getId() + " has been returned.");
    }

    // ─── 4. Rental history ───────────────────────────────────────────
    private static void showRentalHistory() {
        System.out.println("\n── Rental History ──");
        if (rentalHistory.isEmpty()) {
            System.out.println("  (empty so far)");
            return;
        }
        for (int i = 0; i < rentalHistory.size(); i++) {
            RentalAgreement a = rentalHistory.get(i);
            String status = a.getVehicle().isAvailable() ? "returned" : "active";
            System.out.printf("  %d. %s | %s | %s | $%.2f | %s%n",
                    i + 1, a.getCustomer().getName(), a.getVehicle().getType(),
                    a.getHours() + " h", a.getTotalCost(), status);
        }
    }

    // ─── 5. Price calculator ─────────────────────────────────────────
    private static void calculatePricePreview() {
        System.out.println("\n── Price Calculator ──");
        System.out.println("Available vehicle types:");
        System.out.println("  1. Car ($20/hour)");
        System.out.println("  2. Bike ($8/hour)");
        System.out.println("  3. ElectricScooter ($5/hour)");

        int typeChoice = readInt("Choose a type: ");
        String type = switch (typeChoice) {
            case 1 -> "Car";
            case 2 -> "Bike";
            case 3 -> "ElectricScooter";
            default -> null;
        };
        if (type == null) {
            System.out.println("Invalid choice.");
            return;
        }

        int hours = readInt("Number of hours: ");
        if (hours <= 0) {
            System.out.println("Hours must be > 0.");
            return;
        }

        Vehicle sample = VehicleFactory.createVehicle(type, "PREVIEW");

        System.out.println("\nPricing for " + type + " for " + hours + " h:");
        for (PricingStrategy s : List.of(new HourlyPricing(), new DailyPricing(), new WeekendPricing())) {
            System.out.printf("  %-32s = $%.2f%n",
                    s.getStrategyName(), s.calculatePrice(sample, hours));
        }
    }

    // ─── Helper to read an int ───────────────────────────────────────
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}