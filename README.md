# Vehicle Rental Service
**Course:** Software Design Patterns  
**Team:** Medina Alish, Akmarzhan Nurtas, Dayana Zharylgapova (SE-2538)  
**Topic:** Vehicle Rental Service

---

## About the Project
This is a console-based Java application built to demonstrate six GoF design patterns working together. Instead of isolated textbook examples, we built a single unified system where a rental request flows through all six patterns. 

We chose a vehicle rental service (cars, bikes, scooters) because it’s simple enough that the focus stays on the *architecture* rather than complicated business rules.

---

## How It Works
### The six patterns
| # | Pattern | Type | Owner | Role in the system |
|---|---------|------|-------|---------------------|
| 1 | **Factory** | Creational | Medina | `VehicleFactory.createVehicle(type, id)` returns `Car` / `Bike` / `ElectricScooter` without exposing constructors. |
| 2 | **Strategy** | Behavioral | Medina | `PricingStrategy` with `HourlyPricing`, `DailyPricing`, `WeekendPricing` — interchangeable price algorithms. |
| 3 | **Builder** | Creational | Akmarzhan | `RentalAgreementBuilder` assembles a `RentalAgreement` step-by-step with a fluent API and a validating `build()`. |
| 4 | **Observer** | Behavioral | Akmarzhan | `AvailabilitySubject` notifies subscribed `AvailabilityObserver`s (e.g. `CustomerNotifier`) when availability changes. |
| 5 | **Decorator** | Structural | Dayana | `RentalService` wrapped by `GPSDecorator`, `ChildSeatDecorator`, `FullInsuranceDecorator` to add cost/description at runtime. |
| 6 | **Facade** | Structural | Dayana | `RentalFacade` exposes `findVehicle`, `createAgreement`, `payAndRent`, `returnVehicle` and coordinates all subsystems. |

---

## Project Structure
```
vehicle-rental-service/
├── README.md
├── docs/
│ ├── Final-Project-Report.pdf
│ └── uml/
│ ├── factory-strategy.png
│ ├── builder-observer.png
│ ├── decorator-facade.png
│ └── overall-architecture.png
└── src/
└── com/rental/
├── RentalApp.java
│
├── model/
│ ├── Vehicle.java
│ ├── Car.java
│ ├── Bike.java
│ ├── ElectricScooter.java
│ ├── Customer.java
│ └── RentalAgreement.java
│
├── factory/
│ └── VehicleFactory.java
│
├── strategy/
│ ├── PricingStrategy.java
│ ├── HourlyPricing.java
│ ├── DailyPricing.java
│ └── WeekendPricing.java
│
├── builder/
│ └── RentalAgreementBuilder.java
│
├── observer/
│ ├── AvailabilityObserver.java
│ ├── AvailabilitySubject.java
│ └── CustomerNotifier.java
│
├── decorator/
│ ├── RentalService.java
│ ├── RentalDecorator.java
│ ├── BaseRental.java
│ ├── GPSDecorator.java
│ ├── ChildSeatDecorator.java
│ └── FullInsuranceDecorator.java
│
└── facade/
└── RentalFacade.java
```

---

### Interactive Console Menu (`RentalApp`)
| # | Menu Option | What It Does |
|---|-------------|--------------|
| 1 | **Show available vehicles** | Lists every vehicle whose `isAvailable()` is `true`, with its ID, type, and hourly rate. |
| 2 | **Create a rental** | Select a vehicle by ID, enter customer name and hours, choose a pricing strategy (Hourly / Daily / Weekend), and pick extras (GPS, Child Seat, Full Insurance). |
| 3 | **Return a vehicle** | Lists all active rentals and returns the selected one to the fleet. |
| 4 | **Rental history** | Displays every agreement with customer, vehicle, hours, cost, and current status (`active` / `returned`). |
| 5 | **Price calculator** | Compares all three pricing strategies side by side for a chosen vehicle type and duration. |
| 6 | **Exit** | Cleanly closes the scanner and terminates the application. |

### Pricing Rules
| Strategy | Formula | Example (Car, $20/h) |
|---|---|---|
| Hourly | `base × hours` | 5 h → **$100.00** |
| Daily | `base × 24 × ⌈hours/24⌉ × 0.8` | 5 h → **$384.00** |
| Weekend | `base × hours × 1.15` | 5 h → **$115.00** |

### Key Architectural Decisions
- **Interface-driven design** — `Vehicle`, `PricingStrategy`, `RentalService`, `AvailabilityObserver` are abstractions.
- **Single Responsibility** — each class has exactly one job.
- **Immutability** — `RentalAgreement` has `final` fields and no setters.
- **Validation** — `RentalAgreementBuilder` throws `IllegalStateException` on missing fields; `VehicleFactory` throws `IllegalArgumentException` on unknown types.
- **Facade ↔ Decorator boundary** — the Facade does *not* touch decorators directly; the Builder assembles the decorated service, keeping the Facade clean.

## Individual Contribution of Member
### Medina Alish — Factory + Strategy
- Implemented the Factory (VehicleFactory) and Strategy (PricingStrategy, HourlyPricing, DailyPricing, WeekendPricing). Also built a standalone Strategy Calculator mini-project.
### Akmarzhan Nurtas — Builder + Observer
- Built the Builder (RentalAgreementBuilder with fluent setters and validation) and the Observer subsystem. Connected the Builder to the Decorators inside addExtra().
### Dayana Zharylgapova — Decorator + Facade
- Built the Decorator subsystem (RentalService, BaseRental, and all three concrete decorators) and the RentalFacade.
  
---

## How to Run
**Prerequisites:** JDK 17 or newer.   
### macOS / Linux:
```
git clone https://github.com/Tokisao/vehicle-rental-service.git
cd vehicle-rental-service
javac -d out $(find src -name "*.java")
java -cp out com.rental.RentalApp
```

### Windows:
```
git clone https://github.com/Tokisao/vehicle-rental-service.git
cd vehicle-rental-service
javac -d out src\com\rental\**\*.java
java -cp out com.rental.RentalApp
```

### Expected Output (App Launch)
When you run the application, you will see the interactive console menu:
```
============================================
   WELCOME TO THE VEHICLE RENTAL SYSTEM
============================================

--- MENU ---
1. Show available vehicles
2. Create a rental
3. Return a vehicle
4. Rental history
5. Price calculator
6. Exit

Choose an option: 1

--- Available Vehicles ---
[+] CAR-001      | Car             | $20.00/hour
[+] BIKE-001     | Bike            | $8.00/hour
[+] SCOOTER-001  | ElectricScooter | $5.00/hour
```

When creating a rental with extras, you can see the Facade payment step and the Observer firing:
```
[Facade] Found vehicle: Car (CAR-001)
[Facade] Agreement created. Total: $125.00

--- Rental Agreement #1 ---
Agreement: Medina | Vehicle: Car | Hours: 5 | Extras: [Base rental for Car (5 hours) + GPS + Full Insurance] | Total: $125.0

Pay and rent now? (y/n): y
[Facade] Payment processed for Medina
[Notification] Vehicle Car has been RENTED.
[Facade] Vehicle successfully rented!
```

---

## Testing
We included a self-contained test suite (TestRunner.java) that requires no external libraries—just standard Java.

### Running Tests
No external dependencies required — just JDK 17+.   
### macOS / Linux:
```
javac -d out $(find src -name "*.java")
java -cp out com.rental.TestRunner
```

### Windows:
```
javac -d out src\com\rental\**\*.java src\com\rental\TestRunner.java
java -cp out com.rental.TestRunner\
```

### Test Coverage
- **Factory** — verifies correct vehicle type creation, case-insensitive input, and `IllegalArgumentException` on unknown types.
- **Strategy** — verifies hourly, daily (with day rounding and 20% discount), and weekend (+15% fee) price calculations, plus strategy names.
- **Builder** — verifies basic agreement construction, extras stacking (GPS +5, Child Seat +3, Full Insurance +15), and `IllegalStateException` when required fields are missing or `hours <= 0`.
- **Observer** — verifies that all attached observers receive notifications and that no notification occurs without `attach`.
- **Decorator** — verifies each decorator's cost modifier and that decorators stack correctly (e.g., GPS + Child Seat + Full Insurance = +23).
- **Facade** — verifies vehicle lookup, agreement creation with all pricing types, extras handling, and the full pay/return flow that updates vehicle availability.
- **Integration** — one end-to-end test confirms that Factory → Builder → Strategy → Decorator → Observer → Facade work together in a single rental flow.

### Expected Output
```
========================================
  Vehicle Rental Service — Test Runner
========================================

--- Factory Pattern ---
  [PASS] Factory creates Car
  [PASS] Factory creates Bike
  [PASS] Factory creates ElectricScooter
  [PASS] Factory is case-insensitive
  [PASS] Factory throws on unknown type
  [PASS] New vehicle is available

--- Strategy Pattern ---
  [PASS] HourlyPricing = base * hours
  [PASS] DailyPricing rounds up days and applies 20% discount
  [PASS] WeekendPricing adds 15%
  [PASS] Strategy names are correct

--- Builder Pattern ---
  [PASS] Builder builds basic agreement
  [PASS] Builder adds GPS (+5)
  [PASS] Builder adds multiple extras
  [PASS] Builder throws without customer
  [PASS] Builder throws without vehicle
  [PASS] Builder throws when hours not positive
  [PASS] Builder throws without strategy

--- Observer Pattern ---
  [PASS] Observer is notified
  [PASS] All observers are notified
  [PASS] No notification without attach

--- Decorator Pattern ---
  [PASS] BaseRental cost
  [PASS] GPSDecorator adds 5
  [PASS] ChildSeatDecorator adds 3
  [PASS] FullInsuranceDecorator adds 15
  [PASS] Decorators stack

--- Facade Pattern ---
  [PASS] Facade findVehicle returns Car
  [PASS] Facade createAgreement calculates price
  [PASS] Facade createAgreement with extras
  [PASS] Facade createAgreement with daily pricing
  [PASS] Facade throws on unknown pricing type
  [PASS] Facade payAndRent makes vehicle unavailable
  [PASS] Facade returnVehicle makes vehicle available again

--- Integration (all patterns together) ---
  [PASS] Full rental flow: Factory -> Builder -> Strategy -> Decorator -> Observer -> Facade

========================================
  32 passed, 0 failed
========================================
```
