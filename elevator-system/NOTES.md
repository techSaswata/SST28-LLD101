# Elevator System — My Notes (LLD: Elevator Design)

---

## What the problem asks to do

Design an elevator system. A building has multiple elevators. People press a button on a floor (requesting UP or DOWN). The system picks the best elevator, sends it there, and then the person selects their destination floor inside the elevator.

---

## The concepts being used

### Strategy Pattern + SRP

Think of a real building with elevators:
- **The elevator** only knows how to move between floors and maintain its list of stops. It doesn't decide which requests it gets — that's someone else's job.
- **The controller** receives all requests and assigns them to elevators. It doesn't move elevators — it just dispatches.
- **The selection strategy** decides *which* elevator gets the request. The controller doesn't care about the algorithm — it just asks the strategy.

**SRP**: Elevator moves. Controller dispatches. Strategy selects. Each has one job.
**OCP**: Want a different selection algorithm (round-robin, least-loaded)? Create a new strategy. Don't touch `Elevator` or `ElevatorController`.
**DIP**: `ElevatorController` depends on `ElevatorSelectionStrategy` interface, not on `NearestElevatorStrategy` directly.

---

## Steps to identify, understand what to do, and what is exactly done to solve it

**Step 1 — Model the direction and requests**

An elevator goes UP, DOWN, or is IDLE. A request comes from a floor and specifies which direction the person wants to go.

We created `Direction` enum with `UP`, `DOWN`, `IDLE`. We created `Request` with `floor` and `direction` fields. Simple value objects — no logic, just data.

**Step 2 — Build the Elevator**

An elevator knows its current floor, its direction, and has two sorted sets of pending stops — one for going up, one for going down. It processes all up-stops in ascending order, then all down-stops in descending order.

We created `Elevator` with `currentFloor`, `direction`, and two `TreeSet<Integer>` fields: `upStops` and `downStops`. `addStop(floor)` puts the floor in the right set based on whether it's above or below current. `move()` pops the next stop from the current direction's set. `processAllStops()` runs through all pending stops — up first, then down — until both sets are empty, then goes IDLE.

**Step 3 — Extract elevator selection behind an interface**

How you pick an elevator for a request is a strategy. The simplest: pick the nearest idle one, or one already heading that way.

We created `ElevatorSelectionStrategy` interface with `select(elevators, request)`. `NearestElevatorStrategy` implements it — it first looks for idle elevators or ones already heading in the right direction that haven't passed the floor yet. If none found, it falls back to the absolute nearest elevator regardless of direction.

**Step 4 — Build the ElevatorController**

The controller owns all elevators and dispatches requests. It also handles internal requests (person inside the elevator pressing a floor button).

We created `ElevatorController` with a `List<Elevator>` and the injected `ElevatorSelectionStrategy`. `handleRequest(request)` asks the strategy to pick an elevator, then adds the stop. `handleInternalRequest(elevator, floor)` adds a destination stop directly. `step()` tells all elevators to process their stops. `status()` prints the state of all elevators.

**Step 5 — Wire it all in Main**

We created a 3-elevator building. Made external requests from different floors (floor 5 going up, floor 3 going down, floor 7 going up), then added internal destinations (8, 1, 10). Called `step()` to process — elevator 1 served floors 1→3→5→7→8→10 in order. Then a second round: floor 2 up went to elevator 2 (nearest idle), floor 9 down went to elevator 1 (nearest at floor 10).

---

## UML Diagram

```
        +---------------------------+
        |    ElevatorController     |
        |---------------------------|
        | - elevators: List         |
        | - strategy                |---> ElevatorSelectionStrategy (interface)
        |---------------------------|
        | + handleRequest(req)      |
        | + handleInternalRequest() |
        | + step()                  |
        | + status()                |
        +-------------+-------------+
                      |
                      | manages
                      v
        +---------------------------+
        |         Elevator          |
        |---------------------------|
        | - id                      |
        | - currentFloor            |
        | - direction               |---> Direction (enum: UP/DOWN/IDLE)
        | - upStops: TreeSet        |
        | - downStops: TreeSet      |
        |---------------------------|
        | + addStop(floor)          |
        | + move()                  |
        | + processAllStops()       |
        +---------------------------+

+----------------------------+
| ElevatorSelectionStrategy  |  (interface)
|----------------------------|
| + select(elevators, req)   |
+-------------+--------------+
              |
+-------------+--------------+
| NearestElevatorStrategy    |
|----------------------------|
| picks nearest idle or      |
| on-the-way elevator        |
+----------------------------+

+-------------------+
|     Request       |
|-------------------|
| - floor           |
| - direction       |
+-------------------+
```

---

## The story in one paragraph

We needed a multi-elevator system where requests from floors get assigned to the best elevator. We split it into three pieces: `Elevator` manages its own floor and sorted stop lists (TreeSet for up, TreeSet for down — so stops are always served in order). `NearestElevatorStrategy` picks which elevator handles a request — it prefers idle elevators or ones already heading in the right direction. `ElevatorController` dispatches requests using the strategy and tells elevators to process their stops. Want a different dispatch algorithm? Just create a new strategy. The elevator and controller don't change.
