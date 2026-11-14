package com.rental;

import com.rental.factory.VehicleFactory;
import com.rental.model.Vehicle;
import com.rental.strategy.*;

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
    }
}