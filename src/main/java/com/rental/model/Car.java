package com.rental.model;

public class Car implements Vehicle {
    private String id;
    private boolean available;

    public Car(String id) {
        this.id = id;
        this.available = true;
    }

    @Override public String getId() { return id; }
    @Override public String getType() { return "Car"; }
    @Override public double getBasePricePerHour() { return 20.0; }
    @Override public boolean isAvailable() { return available; }
    @Override public void setAvailable(boolean available) { this.available = available; }
}