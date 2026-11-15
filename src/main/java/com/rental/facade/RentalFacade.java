package com.rental.facade;

import com.rental.builder.RentalAgreementBuilder;
import com.rental.factory.VehicleFactory;
import com.rental.model.*;
import com.rental.observer.*;
import com.rental.strategy.*;

public class RentalFacade {
    private final AvailabilitySubject availabilitySubject = new AvailabilitySubject();

    public RentalFacade() {
        // Подключаем наблюдателя (уведомления) внутри фасада
        availabilitySubject.attach(new CustomerNotifier());
    }

    // 1. Подобрать авто
    public Vehicle findVehicle(String type, String id) {
        Vehicle vehicle = VehicleFactory.createVehicle(type, id);
        System.out.println("[Facade] Found vehicle: " + vehicle.getType() + " (" + id + ")");
        return vehicle;
    }

    // 2. Оформить аренду
    public RentalAgreement createAgreement(Customer customer, Vehicle vehicle, int hours, String pricingType, String... extras) {
        PricingStrategy strategy;
        switch (pricingType.toLowerCase()) {
            case "hourly": strategy = new HourlyPricing(); break;
            case "daily": strategy = new DailyPricing(); break;
            case "weekend": strategy = new WeekendPricing(); break;
            default: throw new IllegalArgumentException("Unknown pricing type");
        }

        RentalAgreementBuilder builder = new RentalAgreementBuilder()
                .setCustomer(customer)
                .setVehicle(vehicle)
                .setHours(hours)
                .setStrategy(strategy);

        for (String extra : extras) {
            builder.addExtra(extra);
        }

        RentalAgreement agreement = builder.build();
        System.out.println("[Facade] Agreement created. Total: $" + agreement.getTotalCost()); // если добавите геттер
        return agreement;
    }

    // 3. Оплатить (и запустить процесс аренды)
    public void payAndRent(RentalAgreement agreement) {
        // Логика оплаты (заглушка)
        System.out.println("[Facade] Payment processed for " + agreement.getCustomer().getName());
        
        // Меняем статус машины и уведомляем наблюдателей
        agreement.getVehicle().setAvailable(false);
        availabilitySubject.notifyObservers(agreement.getVehicle(), false);
        
        System.out.println("[Facade] Vehicle successfully rented!");
    }
    
    // Метод для возврата (для полноты)
    public void returnVehicle(RentalAgreement agreement) {
        agreement.getVehicle().setAvailable(true);
        availabilitySubject.notifyObservers(agreement.getVehicle(), true);
        System.out.println("[Facade] Vehicle returned.");
    }
}