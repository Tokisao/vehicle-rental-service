package com.rental.model;

public class Bike implements Vehicle {
    private String id;
    private boolean available;

    public Bike(String id) {
        this.id = id;
        this.available = true;
    }

    @Override public String getId() { return id; }
    @Override public String getType() { return "Bike"; }
    @Override public double getBasePricePerHour() { return 8.0; }
    @Override public boolean isAvailable() { return available; }
    @Override public void setAvailable(boolean available) { this.available = available; }
}