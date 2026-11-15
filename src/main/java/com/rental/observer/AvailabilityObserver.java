package com.rental.observer;

import com.rental.model.Vehicle;

public interface AvailabilityObserver {
    void update(Vehicle vehicle, boolean isAvailable);
}
