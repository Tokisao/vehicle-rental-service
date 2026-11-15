package com.rental.decorator;

public class FullInsuranceDecorator extends RentalDecorator {
    public FullInsuranceDecorator(RentalService decoratedService) { super(decoratedService); }
    
    @Override
    public double getCost() { return super.getCost() + 15.0; }
    
    @Override
    public String getDescription() { return super.getDescription() + " + Full Insurance"; }
}