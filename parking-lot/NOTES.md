# Parking Lot — My Notes (LLD: Multilevel Parking Lot)

---

## What the problem asks to do

Design a multilevel parking lot system that supports:
1. Three slot types: **Small** (2-wheelers), **Medium** (cars), **Large** (buses)
2. A smaller vehicle can park in a larger slot (bike in medium, car in large)
3. Billing is based on **slot type**, not vehicle type (bike in medium slot = medium rate)
4. Multiple entry gates — always assign the **nearest available compatible slot**
5. Three APIs: `park(...)`, `status()`, `exit(...)`

Use all SOLID principles.

---

## The concepts being used

### SOLID Principles + Strategy Pattern

Think of a real parking lot:
- **SRP**: The parking lot orchestrates. The fee calculator only calculates fees. The slot assignment strategy only finds slots. Each class has one job.
- **OCP**: Want a different fee model (flat rate, per-minute)? Create a new `FeeCalculator` implementation. Don't touch `ParkingLot.java`.
- **LSP**: `HourlyFeeCalculator` can be swapped for any other `FeeCalculator` — same interface, no surprises.
- **ISP**: Each interface (`FeeCalculator`, `SlotAssignmentStrategy`) has only the methods it needs. No fat interfaces.
- **DIP**: `ParkingLot` depends on `FeeCalculator` and `SlotAssignmentStrategy` interfaces — not on `HourlyFeeCalculator` or `NearestSlotStrategy` directly.

---

## Steps to identify, understand what to do, and what is exactly done to solve it

**Step 1 — Model the core entities**

A parking lot has vehicles, slots, and tickets. Each of these is a different concept with its own data.

We created `VehicleType` enum (`TWO_WHEELER`, `CAR`, `BUS`) — each entry stores a list of compatible slot types (e.g., `TWO_WHEELER` can fit in `SMALL`, `MEDIUM`, or `LARGE`). `SlotType` enum has `SMALL`, `MEDIUM`, `LARGE`. `Vehicle` holds a license plate and type. `ParkingSlot` holds slot number, type, floor, distance from gate, and an `occupied` flag. `ParkingTicket` stores the vehicle, the assigned slot, and the entry time.

**Step 2 — Extract fee calculation behind an interface**

Different parking lots charge differently — some hourly, some flat, some per-minute. If we hard-code the fee formula inside the parking lot, we'd have to change the parking lot every time the pricing model changes.

We created `FeeCalculator` interface with `calculate(SlotType, long hours)`. `HourlyFeeCalculator` implements it with a `HashMap<SlotType, Double>` mapping each slot type to its hourly rate (Small=10, Medium=20, Large=40). It bills by slot type, not vehicle type — so a bike in a medium slot pays the medium rate.

**Step 3 — Extract slot assignment behind an interface**

"Find the nearest compatible slot" is a strategy that could change. Maybe later you want "find the cheapest slot" or "find on the same floor".

We created `SlotAssignmentStrategy` interface with `findSlot(slots, vehicleType, entryGateId)`. `NearestSlotStrategy` implements it — it loops through all slots, filters for unoccupied + compatible type, then picks the one with the smallest distance from the entry gate.

**Step 4 — Build the ParkingLot orchestrator**

The parking lot itself just coordinates: find a slot, occupy it, create a ticket, store it. On exit: calculate duration, calculate fee, free the slot.

We created `ParkingLot` with three constructor-injected dependencies: `List<ParkingSlot>`, `SlotAssignmentStrategy`, and `FeeCalculator`. It has a `Map<String, ParkingTicket>` for active tickets. `park()` delegates to the strategy, creates a ticket, stores it. `exit()` calculates hours via `Duration.between()`, delegates fee to the calculator, frees the slot. `status()` counts total vs available per slot type.

**Step 5 — Wire it all together in Main**

We built a 2-floor lot (Floor 1: 3 small, 3 medium, 2 large; Floor 2: 2 small, 2 medium, 2 large = 14 total slots). Parked a bike, a car, and a bus. Then filled all small slots and showed a bike spilling into a medium slot. On exit, confirmed the bike-in-medium was billed at the medium rate (₹60 for 3 hours, not ₹30).

---

## UML Diagram

```
                  +--------------------+
                  |    ParkingLot      |
                  |--------------------|
                  | - slots            |
                  | - activeTickets    |
                  | - ticketCounter    |
                  |--------------------|
                  | + park(...)        |  → ParkingTicket
                  | + exit(...)        |  → double (fee)
                  | + status()         |  → Map<SlotType, int[]>
                  +---------+----------+
                            |
              +-------------+-------------+
              |                           |
   +----------+----------+    +-----------+---------+
   |SlotAssignmentStrategy|    |   FeeCalculator    |
   |   (interface)        |    |   (interface)      |
   |----------------------|    |-------------------|
   | findSlot(...)        |    | calculate(...)    |
   +----------+-----------+    +---------+---------+
              |                          |
   +----------+-----------+    +---------+---------+
   | NearestSlotStrategy  |    |HourlyFeeCalculator|
   |----------------------|    |-------------------|
   | loops slots, picks   |    | hourlyRates map   |
   | nearest compatible   |    | rate * hours      |
   +----------------------+    +-------------------+

+----------+     +-----------+     +-------------+
| Vehicle  |     |ParkingSlot|     |ParkingTicket|
|----------|     |-----------|     |-------------|
| plate    |     | slotNumber|     | ticketId    |
| type     |     | type      |     | vehicle     |
+----------+     | floor     |     | slot        |
                 | distance  |     | entryTime   |
  +----------+   | occupied  |     +-------------+
  |VehicleType|  +-----------+
  |----------|
  |TWO_WHEELER| → [SMALL, MEDIUM, LARGE]
  |CAR       | → [MEDIUM, LARGE]
  |BUS       | → [LARGE]
  +----------+
```

---

## The story in one paragraph

We needed a multilevel parking lot with three slot sizes, multiple entry gates, and billing by slot type. Instead of one giant class, we split responsibilities: `ParkingLot` orchestrates, `NearestSlotStrategy` finds the best slot, `HourlyFeeCalculator` computes the fee. Vehicle-to-slot compatibility lives inside the `VehicleType` enum — each vehicle type knows which slot types it fits in. When a bike parks in a medium slot (because all small slots are full), it gets billed at the medium rate, not the bike rate. The `park()` / `status()` / `exit()` APIs are clean — the lot delegates to its strategy and calculator, both injected via constructor. Want a different fee model? Swap the calculator. Want a different assignment strategy? Swap the strategy. Nothing else changes.
