# Ex10 — My Notes (DIP Fix: Campus Transport Booking)

---

## What the problem asks to do

A student books a campus cab ride. The system needs to:
1. Calculate the distance between pickup and drop points
2. Allocate a driver
3. Calculate the fare
4. Process payment
5. Print a receipt

The problem says: **`TransportBookingService.book` directly creates `DistanceCalculator`, `DriverAllocator`, and `PaymentGateway` using `new` inside the method**. Fix it so the booking service depends on abstractions, not concrete classes.

---

## The concept being used: DIP (Dependency Inversion Principle)

Think of a food delivery app. The app doesn't care if the restaurant uses gas stoves or electric ones. It doesn't care if the delivery person rides a bike or a scooter. The app just knows: "someone cooks it, someone delivers it".

If the app was hardwired to only work with one specific restaurant and one specific delivery person, adding a new partner would require rewriting the app.

**DIP says:** High-level code (booking logic) should depend on **interfaces**, not on specific implementations. The actual classes (concrete distance calculator, driver allocator) are plugged in from outside.

---

## What was broken (Before the fix)

`TransportBookingService.book` looked like:
```java
DistanceCalculator dist = new DistanceCalculator();
DriverAllocator alloc = new DriverAllocator();
PaymentGateway pay = new PaymentGateway();
```

And the fare was computed inline with messy magic numbers:
```java
double fare = 50.0 + km * 6.6666666667;
```

Problems:
- Pricing logic is buried inside booking logic — two different concerns in one place
- Can't swap the payment gateway without editing the booking service
- Can't test the booking logic with a fake allocator
- The booking service knows too much about how each piece works

---

## Steps to identify and understand what to do

**Step 1 — Identify the 4 roles**

- Something calculates distance → `DistanceCalculatorService` interface
- Something allocates a driver → `DriverAllocatorService` interface
- Something processes payment → `PaymentGatewayService` interface
- Something calculates fare → `FareCalculator` interface (new — pulled out of the inline formula)

**Step 2 — Create interfaces for each role**

Each has one method that the booking service needs.

**Step 3 — Make concrete classes implement the interfaces**

`DistanceCalculator implements DistanceCalculatorService`
`DriverAllocator implements DriverAllocatorService`
`PaymentGateway implements PaymentGatewayService`
`DefaultFareCalculator implements FareCalculator` (new class that holds the pricing formula)

**Step 4 — Inject via constructor into TransportBookingService**

The booking service stores them as fields. No `new` inside `book()` anymore.

**Step 5 — Main wires everything**

`Main` creates all the concretes and passes them in. The booking service just calls the interfaces — it doesn't know what's behind them.

---

## UML Diagram

```
              +----------------------------+
              | TransportBookingService    |
              |----------------------------|
              | - distanceCalculator       |---> DistanceCalculatorService (interface)
              | - driverAllocator          |---> DriverAllocatorService (interface)
              | - paymentGateway           |---> PaymentGatewayService (interface)
              | - fareCalculator           |---> FareCalculator (interface)
              |----------------------------|
              | + book(TripRequest)        |
              +----------------------------+

DistanceCalculatorService    DriverAllocatorService    PaymentGatewayService    FareCalculator
        ^                            ^                        ^                     ^
        |                            |                        |                     |
 DistanceCalculator           DriverAllocator           PaymentGateway   DefaultFareCalculator
 (fake Manhattan dist)        (returns DRV-17)          (returns TXN)    (50 + km * 6.67)

              +------------------------+
              |         Main           |
              |------------------------|
              | creates all concretes  |
              | wires them together    |
              | passes to booking svc  |
              +------------------------+
```

---

## The story in one paragraph

`TransportBookingService.book` was doing `new DistanceCalculator()`, `new DriverAllocator()`, `new PaymentGateway()` and also had the pricing formula hardcoded inline. We created interfaces for each role: `DistanceCalculatorService`, `DriverAllocatorService`, `PaymentGatewayService`, and a new `FareCalculator` interface to hold the pricing logic separately. The booking service now takes all four as constructor arguments and only talks to the interfaces. `Main` creates the real implementations and passes them in. The booking logic itself never changes if you swap the payment provider or the fare calculation. That's DIP: the high-level booking logic depends on abstractions, and the low-level implementations can be swapped freely.
