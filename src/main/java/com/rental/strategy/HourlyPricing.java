package com.rental.strategy;

import com.rental.model.Vehicle;

public class HourlyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle, int hours) {
        return vehicle.getBasePricePerHour() * hours;
    }

    @Override
    public String getStrategyName() {
        return "Hourly Pricing";
    }
}