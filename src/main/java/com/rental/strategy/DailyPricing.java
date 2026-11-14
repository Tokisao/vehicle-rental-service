package com.rental.strategy;

import com.rental.model.Vehicle;

public class DailyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(Vehicle vehicle, int hours) {
        int days = (int) Math.ceil(hours / 24.0); //calculate how many full or partial days
        return (vehicle.getBasePricePerHour() * 24 * days) * 0.8; //give a 20% discount for daily bookings
    }

    @Override
    public String getStrategyName() {
        return "Daily Pricing (20% discount)";
    }
}