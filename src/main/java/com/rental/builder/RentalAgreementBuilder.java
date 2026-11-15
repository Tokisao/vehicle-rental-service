package com.rental.builder;

import com.rental.decorator.*;
import com.rental.model.Customer;
import com.rental.model.RentalAgreement;
import com.rental.model.Vehicle;
import com.rental.strategy.PricingStrategy;

public class RentalAgreementBuilder {
    private Customer customer;
    private Vehicle vehicle;
    private int hours;
    private PricingStrategy strategy;
    private RentalService rentalService; 

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
        if (this.rentalService == null) {
            this.rentalService = new BaseRental(this.vehicle, this.hours, this.strategy);
        }
        switch (extra) {
            case "GPS": 
                this.rentalService = new GPSDecorator(this.rentalService); 
                break;
            case "Child Seat": 
                this.rentalService = new ChildSeatDecorator(this.rentalService); 
                break;
            case "Full Insurance": 
                this.rentalService = new FullInsuranceDecorator(this.rentalService); 
                break;
        }
        return this;
    }

    public RentalAgreement build() {
        if (customer == null || vehicle == null || strategy == null || hours <= 0) {
            throw new IllegalStateException("Missing required fields for RentalAgreement");
        }
        if (rentalService == null) {
            rentalService = new BaseRental(vehicle, hours, strategy);
        }

        double totalCost = rentalService.getCost();
        String extrasDescription = rentalService.getDescription();

        return new RentalAgreement(customer, vehicle, hours, extrasDescription, totalCost);
    }
}