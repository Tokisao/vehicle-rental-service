package com.rental.factory;

import com.rental.model.*;

public class VehicleFactory {

    public static Vehicle createVehicle(String type, String id) {
        if (type.equalsIgnoreCase("Car")) {
            return new Car(id);
        } else if (type.equalsIgnoreCase("Bike")) {
            return new Bike(id);
        } else if (type.equalsIgnoreCase("ElectricScooter")) {
            return new ElectricScooter(id);
        } else {
            throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}