package com.rental.model;

public class ElectricScooter implements Vehicle {
    private String id;
    private boolean available;

    public ElectricScooter(String id) {
        this.id = id;
        this.available = true;
    }

    @Override public String getId() { return id; }
    @Override public String getType() { return "ElectricScooter"; }
    @Override public double getBasePricePerHour() { return 5.0; } // 5 currency per hour
    @Override public boolean isAvailable() { return available; }
    @Override public void setAvailable(boolean available) { this.available = available; }
}