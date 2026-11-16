package test.com.rental;

import com.rental.builder.RentalAgreementBuilder;
import com.rental.decorator.*;
import com.rental.facade.RentalFacade;
import com.rental.factory.VehicleFactory;
import com.rental.model.*;
import com.rental.observer.AvailabilitySubject;
import com.rental.strategy.*;

public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Vehicle Rental Service - Test Runner");
        System.out.println("========================================\n");

        // ============ FACTORY ============
        section("Factory Pattern");

        test("Factory creates Car", () -> {
            Vehicle v = VehicleFactory.createVehicle("Car", "C1");
            check(v instanceof Car);
            check(v.getId().equals("C1"));
            check(v.getType().equals("Car"));
            check(v.getBasePricePerHour() == 20.0);
        });

        test("Factory creates Bike", () -> {
            Vehicle v = VehicleFactory.createVehicle("Bike", "B1");
            check(v instanceof Bike);
            check(v.getBasePricePerHour() == 8.0);
        });

        test("Factory creates ElectricScooter", () -> {
            Vehicle v = VehicleFactory.createVehicle("ElectricScooter", "S1");
            check(v instanceof ElectricScooter);
            check(v.getBasePricePerHour() == 5.0);
        });

        test("Factory is case-insensitive", () -> {
            check(VehicleFactory.createVehicle("car", "C1") instanceof Car);
            check(VehicleFactory.createVehicle("BIKE", "B1") instanceof Bike);
        });

        test("Factory throws on unknown type", () -> {
            try {
                VehicleFactory.createVehicle("Plane", "P1");
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                // ok
            }
        });

        test("New vehicle is available", () -> {
            check(VehicleFactory.createVehicle("Car", "C1").isAvailable());
        });

        // ============ STRATEGY ============
        section("Strategy Pattern");

        test("HourlyPricing = base * hours", () -> {
            Vehicle car = new Car("C1"); // 20/hour
            check(new HourlyPricing().calculatePrice(car, 5) == 100.0);
        });

        test("DailyPricing rounds up days and applies 20% discount", () -> {
            Vehicle car = new Car("C1");
            // 5h -> 1 day -> 20*24*1*0.8 = 384
            check(Math.abs(new DailyPricing().calculatePrice(car, 5) - 384.0) < 0.001);
            // 25h -> 2 days -> 20*24*2*0.8 = 768
            check(Math.abs(new DailyPricing().calculatePrice(car, 25) - 768.0) < 0.001);
        });

        test("WeekendPricing adds 15%", () -> {
            Vehicle car = new Car("C1");
            check(Math.abs(new WeekendPricing().calculatePrice(car, 5) - 115.0) < 0.001);
        });

        test("Strategy names are correct", () -> {
            check(new HourlyPricing().getStrategyName().equals("Hourly Pricing"));
            check(new DailyPricing().getStrategyName().equals("Daily Pricing (20% discount)"));
            check(new WeekendPricing().getStrategyName().equals("Weekend Pricing (+15% fee)"));
        });

        // ============ BUILDER ============
        section("Builder Pattern");

        test("Builder builds basic agreement", () -> {
            RentalAgreement a = new RentalAgreementBuilder()
                    .setCustomer(new Customer("Alice"))
                    .setVehicle(new Car("C1"))
                    .setHours(5)
                    .setStrategy(new HourlyPricing())
                    .build();
            check(a.getCustomer().getName().equals("Alice"));
            check(a.getVehicle().getId().equals("C1"));
            check(a.getHours() == 5);
            check(a.getTotalCost() == 100.0);
        });

        test("Builder adds GPS (+5)", () -> {
            RentalAgreement a = new RentalAgreementBuilder()
                    .setCustomer(new Customer("A"))
                    .setVehicle(new Car("C1"))
                    .setHours(5)
                    .setStrategy(new HourlyPricing())
                    .addExtra("GPS")
                    .build();
            check(a.getTotalCost() == 105.0);
            check(a.getExtras().contains("GPS"));
        });

        test("Builder adds multiple extras", () -> {
            RentalAgreement a = new RentalAgreementBuilder()
                    .setCustomer(new Customer("A"))
                    .setVehicle(new Car("C1"))
                    .setHours(5)
                    .setStrategy(new HourlyPricing())
                    .addExtra("GPS")
                    .addExtra("Child Seat")
                    .addExtra("Full Insurance")
                    .build();
            check(a.getTotalCost() == 123.0); // 100 + 5 + 3 + 15
        });

        test("Builder throws without customer", () -> {
            try {
                new RentalAgreementBuilder()
                        .setVehicle(new Car("C1")).setHours(5)
                        .setStrategy(new HourlyPricing()).build();
                fail("expected IllegalStateException");
            } catch (IllegalStateException e) { /* ok */ }
        });

        test("Builder throws without vehicle", () -> {
            try {
                new RentalAgreementBuilder()
                        .setCustomer(new Customer("A")).setHours(5)
                        .setStrategy(new HourlyPricing()).build();
                fail("expected IllegalStateException");
            } catch (IllegalStateException e) { /* ok */ }
        });

        test("Builder throws when hours not positive", () -> {
            try {
                new RentalAgreementBuilder()
                        .setCustomer(new Customer("A")).setVehicle(new Car("C1"))
                        .setHours(0).setStrategy(new HourlyPricing()).build();
                fail("expected IllegalStateException");
            } catch (IllegalStateException e) { /* ok */ }
        });

        test("Builder throws without strategy", () -> {
            try {
                new RentalAgreementBuilder()
                        .setCustomer(new Customer("A")).setVehicle(new Car("C1"))
                        .setHours(5).build();
                fail("expected IllegalStateException");
            } catch (IllegalStateException e) { /* ok */ }
        });

        // ============ OBSERVER ============
        section("Observer Pattern");

        test("Observer is notified", () -> {
            AvailabilitySubject subject = new AvailabilitySubject();
            final int[] calls = {0};
            final boolean[] lastStatus = {true};
            subject.attach((v, available) -> { calls[0]++; lastStatus[0] = available; });
            subject.notifyObservers(new Car("C1"), false);
            check(calls[0] == 1);
            check(lastStatus[0] == false);
        });

        test("All observers are notified", () -> {
            AvailabilitySubject subject = new AvailabilitySubject();
            final int[] calls = {0};
            subject.attach((v, a) -> calls[0]++);
            subject.attach((v, a) -> calls[0]++);
            subject.attach((v, a) -> calls[0]++);
            subject.notifyObservers(new Car("C1"), true);
            check(calls[0] == 3);
        });

        test("No notification without attach", () -> {
            AvailabilitySubject subject = new AvailabilitySubject();
            final int[] calls = {0};
            subject.notifyObservers(new Car("C1"), false);
            check(calls[0] == 0);
        });

        // ============ DECORATOR ============
        section("Decorator Pattern");

        test("BaseRental cost", () -> {
            RentalService base = new BaseRental(new Car("C1"), 5, new HourlyPricing());
            check(base.getCost() == 100.0);
        });

        test("GPSDecorator adds 5", () -> {
            RentalService s = new GPSDecorator(
                    new BaseRental(new Car("C1"), 5, new HourlyPricing()));
            check(s.getCost() == 105.0);
            check(s.getDescription().contains("GPS"));
        });

        test("ChildSeatDecorator adds 3", () -> {
            RentalService s = new ChildSeatDecorator(
                    new BaseRental(new Car("C1"), 5, new HourlyPricing()));
            check(s.getCost() == 103.0);
            check(s.getDescription().contains("Child Seat"));
        });

        test("FullInsuranceDecorator adds 15", () -> {
            RentalService s = new FullInsuranceDecorator(
                    new BaseRental(new Car("C1"), 5, new HourlyPricing()));
            check(s.getCost() == 115.0);
            check(s.getDescription().contains("Full Insurance"));
        });

        test("Decorators stack", () -> {
            RentalService s = new FullInsuranceDecorator(
                    new ChildSeatDecorator(
                            new GPSDecorator(
                                    new BaseRental(new Car("C1"), 5, new HourlyPricing()))));
            check(s.getCost() == 123.0);
        });

        // ============ FACADE ============
        section("Facade Pattern");

        test("Facade findVehicle returns Car", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle v = f.findVehicle("Car", "C1");
            check(v instanceof Car);
            check(v.getId().equals("C1"));
        });

        test("Facade createAgreement calculates price", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle car = f.findVehicle("Car", "C1"); // 20/hour
            RentalAgreement a = f.createAgreement(new Customer("A"), car, 5, "hourly");
            check(a.getTotalCost() == 100.0);
        });

        test("Facade createAgreement with extras", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle car = f.findVehicle("Car", "C1");
            RentalAgreement a = f.createAgreement(
                    new Customer("A"), car, 5, "hourly", "GPS", "Full Insurance");
            check(a.getTotalCost() == 120.0); // 100 + 5 + 15
        });

        test("Facade createAgreement with daily pricing", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle car = f.findVehicle("Car", "C1");
            RentalAgreement a = f.createAgreement(new Customer("A"), car, 5, "daily");
            check(Math.abs(a.getTotalCost() - 384.0) < 0.001);
        });

        test("Facade throws on unknown pricing type", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle car = f.findVehicle("Car", "C1");
            try {
                f.createAgreement(new Customer("A"), car, 5, "weekly");
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException e) { /* ok */ }
        });

        test("Facade payAndRent makes vehicle unavailable", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle car = f.findVehicle("Car", "C1");
            RentalAgreement a = f.createAgreement(new Customer("A"), car, 5, "hourly");
            check(car.isAvailable());
            f.payAndRent(a);
            check(!car.isAvailable());
        });

        test("Facade returnVehicle makes vehicle available again", () -> {
            RentalFacade f = new RentalFacade();
            Vehicle car = f.findVehicle("Car", "C1");
            RentalAgreement a = f.createAgreement(new Customer("A"), car, 5, "hourly");
            f.payAndRent(a);
            f.returnVehicle(a);
            check(car.isAvailable());
        });

        // ============ INTEGRATION ============
        section("Integration (all patterns together)");

        test("Full rental flow: Factory -> Builder -> Strategy -> Decorator -> Observer -> Facade", () -> {
            RentalFacade facade = new RentalFacade();
            Vehicle car = facade.findVehicle("Car", "CAR-001");
            check(car.isAvailable());

            RentalAgreement agreement = facade.createAgreement(
                    new Customer("Alice"), car, 5, "hourly", "GPS", "Full Insurance");
            check(agreement.getTotalCost() == 120.0); // 20*5 + 5 + 15

            facade.payAndRent(agreement);
            check(!car.isAvailable());

            facade.returnVehicle(agreement);
            check(car.isAvailable());
        });

        // ============ SUMMARY ============
        System.out.println("\n========================================");
        System.out.println("  " + passed + " passed, " + failed + " failed");
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------- mini test framework ----------

    private static void section(String name) {
        System.out.println("\n--- " + name + " ---");
    }

    private static void test(String name, Runnable r) {
        try {
            r.run();
            System.out.println("  [PASS] " + name);
            passed++;
        } catch (Throwable t) {
            System.out.println("  [FAIL] " + name + "  ->  " + t.getMessage());
            failed++;
        }
    }

    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("assertion failed");
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }
}