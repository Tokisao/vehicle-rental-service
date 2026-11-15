package com.rental.observer;

import com.rental.model.Vehicle;

public class CustomerNotifier implements AvailabilityObserver {
    @Override
    public void update(Vehicle vehicle, boolean isAvailable) {
        if (isAvailable) {
            System.out.println("[Notification] Vehicle " + vehicle.getType() + " is now AVAILABLE.");
        } else {
            System.out.println("[Notification] Vehicle " + vehicle.getType() + " has been RENTED.");
        }
    }
}