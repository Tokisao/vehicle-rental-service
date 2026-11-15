package com.rental;

import com.rental.builder.RentalAgreementBuilder;
import com.rental.facade.RentalFacade;
import com.rental.factory.VehicleFactory;
import com.rental.model.Customer;
import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;
import com.rental.observer.AvailabilitySubject;
import com.rental.observer.CustomerNotifier;
import com.rental.strategy.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("    VEHICLE RENTAL SYSTEM - DESIGN PATTERNS DEMO     ");
        System.out.println("=====================================================");

        // ---------------------------------------------------------
        System.out.println("\n    1. Factory Pattern");
        System.out.println("Creating vehicles using VehicleFactory:");
        Vehicle car = VehicleFactory.createVehicle("Car", "CAR-001");
        Vehicle bike = VehicleFactory.createVehicle("Bike", "BIKE-002");
        Vehicle scooter = VehicleFactory.createVehicle("ElectricScooter", "SAM-003");
        
        System.out.println("Created: " + car.getType() + " with ID " + car.getId());
        System.out.println("Created: " + bike.getType() + " with ID " + bike.getId());
        System.out.println("Created: " + scooter.getType() + " with ID " + scooter.getId());

        // ---------------------------------------------------------
        System.out.println("\n    2. Strategy Pattern");
        System.out.println("Testing Pricing Strategies for " + car.getType() + " for 30 hours:");
        
        PricingStrategy hourly = new HourlyPricing();
        PricingStrategy daily = new DailyPricing();
        PricingStrategy weekend = new WeekendPricing();

        System.out.println(hourly.getStrategyName() + ": $" + hourly.calculatePrice(car, 30));
        System.out.println(daily.getStrategyName() + ": $" + daily.calculatePrice(car, 30));
        System.out.println(weekend.getStrategyName() + ": $" + weekend.calculatePrice(car, 30));

        // ---------------------------------------------------------
        System.out.println("\n   3. Decorator Pattern");
        System.out.println("Building a Rental Agreement with extras (GPS, Child Seat):");
        
        Customer alice = new Customer("Alice");
        
        // Собираем договор. Декоратор должен пересчитать цену с учетом услуг!
        RentalAgreement agreement = new RentalAgreementBuilder()
                .setCustomer(alice)
                .setVehicle(car)
                .setHours(30)
                .setStrategy(new HourlyPricing()) // 30 * 20 = 600
                .addExtra("GPS")                  // + 5
                .addExtra("Child Seat")           // + 3
                .build();

        System.out.println(agreement);

        // ---------------------------------------------------------
        System.out.println("\n   4. Observer Pattern");
        System.out.println("Setting up notifications for vehicle availability:");
        
        AvailabilitySubject subject = new AvailabilitySubject();
        subject.attach(new CustomerNotifier());

        System.out.println("\n[Action] Renting the car...");
        car.setAvailable(false);
        subject.notifyObservers(car, false);

        System.out.println("\n[Action] Returning the car...");
        car.setAvailable(true);
        subject.notifyObservers(car, true);

        // ---------------------------------------------------------
        System.out.println("\n 5. Facade Pattern");
        System.out.println("Using Facade to hide subsystem complexity:");
        System.out.println("(Step 1: Find) -> (Step 2: Create) -> (Step 3: Pay)");

        RentalFacade facade = new RentalFacade();
        Customer bob = new Customer("Bob");
        
        Vehicle facadeCar = facade.findVehicle("Bike", "BIKE-999");
        RentalAgreement facadeAgreement = facade.createAgreement(
                bob, facadeCar, 10, "weekend", "Full Insurance"
        );
        facade.payAndRent(facadeAgreement);
        
        System.out.println("\n                   DEMO COMPLETED                    ");
    }
}