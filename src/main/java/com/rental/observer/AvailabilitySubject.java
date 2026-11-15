package com.rental.observer;

import com.rental.model.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class AvailabilitySubject {
    private final List<AvailabilityObserver> observers = new ArrayList<>();

    public void attach(AvailabilityObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Vehicle vehicle, boolean isAvailable) {
        for (AvailabilityObserver observer : observers) {
            observer.update(vehicle, isAvailable);
        }
    }
}