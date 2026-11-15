package com.rental.decorator;

public abstract class RentalDecorator implements RentalService {
    protected RentalService decoratedService;

    public RentalDecorator(RentalService decoratedService) {
        this.decoratedService = decoratedService;
    }

    @Override
    public double getCost() {
        return decoratedService.getCost();
    }

    @Override
    public String getDescription() {
        return decoratedService.getDescription();
    }
}