package com.rental.model;

public interface Vehicle {
    String getId();
    String getType();
    double getBasePricePerHour();
    boolean isAvailable();
    void setAvailable(boolean available);
}