// base realizatoin (rent)
package com.rental.decorator;

import com.rental.model.Vehicle;
import com.rental.strategy.PricingStrategy;

public class BaseRental implements RentalService {
    private final Vehicle vehicle;
    private final int hours;
    private final PricingStrategy strategy;

    public BaseRental(Vehicle vehicle, int hours, PricingStrategy strategy) {
        this.vehicle = vehicle;
        this.hours = hours;
        this.strategy = strategy;
    }

    @Override
    public double getCost() {
        return strategy.calculatePrice(vehicle, hours);
    }

    @Override
    public String getDescription() {
        return "Base rental for " + vehicle.getType() + " (" + hours + " hours)";
    }
}