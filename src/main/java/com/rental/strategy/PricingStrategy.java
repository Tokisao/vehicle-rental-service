package com.rental.strategy;

import com.rental.model.Vehicle;

public interface PricingStrategy {
    double calculatePrice(Vehicle vehicle, int hours);
    String getStrategyName();
}