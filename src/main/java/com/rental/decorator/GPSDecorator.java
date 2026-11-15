// 4. concrete decorator
package com.rental.decorator;

public class GPSDecorator extends RentalDecorator {
    public GPSDecorator(RentalService decoratedService) { super(decoratedService); }
    
    @Override
    public double getCost() { return super.getCost() + 5.0; } // +$5 за GPS
    
    @Override
    public String getDescription() { return super.getDescription() + " + GPS"; }
}