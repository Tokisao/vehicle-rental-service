package com.rental.model;

public class RentalAgreement {
    private final Customer customer;
    private final Vehicle vehicle;
    private final int hours;
    private final String extras;
    private final double totalCost;

    public RentalAgreement(Customer customer, Vehicle vehicle, int hours, String extras, double totalCost) {
        this.customer = customer;
        this.vehicle = vehicle;
        this.hours = hours;
        this.extras = extras;
        this.totalCost = totalCost;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getHours() {
        return hours;
    }

    public String getExtras() {
        return extras;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return "Agreement: " + customer.getName() +
                " | Vehicle: " + vehicle.getType() +
                " | Hours: " + hours +
                " | Extras: [" + extras.trim() + "]" +
                " | Total: $" + totalCost;
    }
}
