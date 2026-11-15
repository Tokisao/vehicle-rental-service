package com.rental.builder;

import com.rental.model.Customer;
import com.rental.model.Vehicle;
import com.rental.model.RentalAgreement;
import com.rental.strategy.PricingStrategy;

public class RentalAgreementBuilder {
    private Customer customer;
    private Vehicle vehicle;
    private int hours;
    private PricingStrategy strategy;
    private String extras = "";

    public RentalAgreementBuilder setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public RentalAgreementBuilder setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public RentalAgreementBuilder setHours(int hours) {
        this.hours = hours;
        return this;
    }

    public RentalAgreementBuilder setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public RentalAgreementBuilder addExtra(String extra) {
        this.extras += extra + " ";
        return this;
    }

    public RentalAgreement build() {
        if (customer == null || vehicle == null || strategy == null || hours <= 0) {
            throw new IllegalStateException("Missing required fields for RentalAgreement");
        }
        double totalCost = strategy.calculatePrice(vehicle, hours);
        return new RentalAgreement(customer, vehicle, hours, extras, totalCost);
    }
}