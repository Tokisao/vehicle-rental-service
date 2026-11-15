package com.rental;

import com.rental.factory.VehicleFactory;
import com.rental.model.Vehicle;
import com.rental.model.Customer;
import com.rental.model.RentalAgreement;
import com.rental.strategy.*;
import com.rental.builder.RentalAgreementBuilder;
import com.rental.observer.AvailabilitySubject;
import com.rental.observer.CustomerNotifier;
public class Main {
    public static void main(String[] args) {
        //factory
        System.out.println("Creating vehicles via Factory:");
        Vehicle car = VehicleFactory.createVehicle("Car", "CAR-001");
        Vehicle bike = VehicleFactory.createVehicle("Bike", "BIKE-002");
        Vehicle scooter = VehicleFactory.createVehicle("ElectricScooter", "SAM-003");

        System.out.println("Created: " + car.getType() + " with ID " + car.getId());
        System.out.println("Created: " + bike.getType() + " with ID " + bike.getId());
        System.out.println("Created: " + scooter.getType() + " with ID " + scooter.getId());

        //strategy
        System.out.println("\nTesting Pricing Strategies for " + car.getType() + " for 30 hours:");

        PricingStrategy hourly = new HourlyPricing();
        PricingStrategy daily = new DailyPricing();
        PricingStrategy weekend = new WeekendPricing();

        System.out.println(hourly.getStrategyName() + ": $" + hourly.calculatePrice(car, 30));
        System.out.println(daily.getStrategyName() + ": $" + daily.calculatePrice(car, 30));
        System.out.println(weekend.getStrategyName() + ": $" + weekend.calculatePrice(car, 30));

        //Observer
        System.out.println("\n==========================================");
        System.out.println("Testing Observer & Builder:");

        AvailabilitySubject subject = new AvailabilitySubject();
        subject.attach(new CustomerNotifier());

        Customer alice = new Customer("Alice");

        System.out.println("\n--- Creating Rental Agreement ---");
        RentalAgreement agreement = new RentalAgreementBuilder()
                .setCustomer(alice)
                .setVehicle(car)
                .setHours(30)
                .setStrategy(new HourlyPricing())
                .addExtra("GPS")
                .addExtra("Child Seat")
                .build();

        System.out.println(agreement);

        System.out.println("\n--- Vehicle Rented ---");
        subject.notifyObservers(car, false);

        System.out.println("\n--- Vehicle Returned ---");
        subject.notifyObservers(car, true);
    }
}