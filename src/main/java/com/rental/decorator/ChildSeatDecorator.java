// abstract decorator
package com.rental.decorator;

public class ChildSeatDecorator extends RentalDecorator {
    public ChildSeatDecorator(RentalService decoratedService) { super(decoratedService); }
    
    @Override
    public double getCost() { return super.getCost() + 3.0; }
    
    @Override
    public String getDescription() { return super.getDescription() + " + Child Seat"; }
}