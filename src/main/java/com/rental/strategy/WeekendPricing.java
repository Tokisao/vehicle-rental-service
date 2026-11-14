package com.rental.strategy;

import com.rental.model.Vehicle;

public class WeekendPricing implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle, int hours) {
        double basePrice = vehicle.getBasePricePerHour() * hours;
        return basePrice * 1.15; //for simplicity in this demo, just add a 15% weekend fee
    }

    @Override
    public String getStrategyName() {
        return "Weekend Pricing (+15% fee)";
    }
}