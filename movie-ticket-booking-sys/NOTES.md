# Movie Ticket Booking — My Notes (LLD: Movie Ticket Booking System)

---

## What the problem asks to do

Design a movie ticket booking system. A theatre has screens with seats of different types (regular, premium, VIP). Movies are scheduled as shows on screens at specific times. Users can:
1. See available seats for a show
2. Book one or more seats
3. Cancel a booking (seats become available again)

Pricing depends on seat type.

---

## The concepts being used

### SRP + Strategy Pattern

Think of a real cinema:
- **The show** knows which seats are taken and which are free. It doesn't calculate prices or manage booking records.
- **The booking service** handles the booking flow — check availability, lock seats, calculate price, create a record. It doesn't know how to calculate prices itself.
- **The price calculator** only does math — given a list of seats, return the total. It doesn't care about shows or booking status.

**SRP**: Show tracks seat availability. BookingService manages bookings. PriceCalculator does pricing. Screen generates the seat layout. Each has one job.
**OCP**: Want different pricing (weekend surcharge, student discount)? Create a new `PriceCalculator`. Don't touch `BookingService`.
**DIP**: `BookingService` depends on `PriceCalculator` interface, not on `DefaultPriceCalculator` directly.

---

## Steps to identify, understand what to do, and what is exactly done to solve it

**Step 1 — Model the core entities**

A movie has a title and duration. A seat has an ID, row, column, and type. Seat types have fixed prices.

We created `Movie` with `id`, `title`, `durationMinutes`. `SeatType` enum with `REGULAR(150)`, `PREMIUM(250)`, `VIP(400)` — each entry stores its price. `Seat` with `seatId`, `row`, `col`, and `type`. All are simple data holders.

**Step 2 — Build the Screen**

A screen has rows of different seat types. We need to generate the seat layout from configuration (how many regular rows, premium rows, VIP rows, columns).

We created `Screen` with a constructor that takes `screenNumber`, `regularRows`, `premiumRows`, `vipRows`, `cols`. It generates all seats in a loop — regular rows first, then premium, then VIP. Each seat gets an ID like `R3-C5`. The screen holds the full `List<Seat>`.

**Step 3 — Build the Show**

A show ties a movie to a screen at a time. It tracks which seats are booked using a `HashSet<String>` of booked seat IDs.

We created `Show` with `showId`, `movie`, `screen`, `startTime`, and `bookedSeatIds` (a `HashSet`). `isSeatAvailable(seatId)` checks the set. `getAvailableSeats()` filters the screen's seats. `bookSeat(seatId)` adds to the set. `cancelSeat(seatId)` removes from the set. The show is the single source of truth for seat availability.

**Step 4 — Extract pricing behind an interface**

How you calculate the total price could vary — flat rate, dynamic pricing, discounts. Keep it swappable.

We created `PriceCalculator` interface with `calculate(List<Seat>)`. `DefaultPriceCalculator` implements it by summing each seat's type price. `BookingService` gets the calculator injected via constructor.

**Step 5 — Build the BookingService**

The service orchestrates: validate seats → mark as booked → calculate price → create booking record. Cancellation reverses it.

We created `BookingService` with injected `PriceCalculator` and a `Map<String, Booking>` for active bookings. `book(show, seatIds)` checks each seat is available and exists, then books them all, calculates the total, and creates a `Booking` with status `CONFIRMED`. `cancel(bookingId)` frees the seats on the show and sets status to `CANCELLED`. `Booking` holds the show, seats list, total price, and status. `BookingStatus` enum has `CONFIRMED` and `CANCELLED`.

**Step 6 — Wire it all in Main**

We created 2 movies (Interstellar, The Dark Knight), 2 screens (Screen 1: 3 regular + 2 premium + 1 VIP rows x 8 cols = 48 seats; Screen 2: 4+1+1 rows x 10 cols = 60 seats), and 2 shows. Booked 2 regular seats (₹300), booked 1 premium + 1 VIP (₹650), tried booking an already-booked seat (failed), cancelled booking 1, re-booked the freed seat (₹150), and booked 4 seats on show 2 (₹950).

---

## UML Diagram

```
        +---------------------------+
        |      BookingService       |
        |---------------------------|
        | - priceCalculator         |---> PriceCalculator (interface)
        | - bookings: Map           |
        |---------------------------|
        | + book(show, seatIds)     |  → Booking
        | + cancel(bookingId)       |  → boolean
        +-------------+-------------+
                      |
                      | creates
                      v
        +---------------------------+
        |         Booking           |
        |---------------------------|
        | - bookingId               |
        | - show                    |---> Show
        | - seats: List<Seat>       |
        | - totalPrice              |
        | - status                  |---> BookingStatus (CONFIRMED/CANCELLED)
        +---------------------------+

+---------------------------+
|           Show            |
|---------------------------|
| - showId                  |
| - movie                  |---> Movie
| - screen                 |---> Screen
| - startTime              |
| - bookedSeatIds: HashSet |
|---------------------------|
| + isSeatAvailable(id)    |
| + getAvailableSeats()    |
| + bookSeat(id)           |
| + cancelSeat(id)         |
+---------------------------+

+---------------+    +-------------------+    +----------+
|    Screen     |    |       Seat        |    |  Movie   |
|---------------|    |-------------------|    |----------|
| - screenNumber|    | - seatId          |    | - id     |
| - seats: List |    | - row, col        |    | - title  |
+---------------+    | - type → SeatType |    | - duration|
                     +-------------------+    +----------+

+-----------------------+
| PriceCalculator       |  (interface)
|-----------------------|
| + calculate(seats)    |
+-----------+-----------+
            |
+-----------+-----------+
|DefaultPriceCalculator |
|-----------------------|
| sums seat type prices |
+-----------------------+

+-------------------+
|    SeatType       |  (enum)
|-------------------|
| REGULAR(150)      |
| PREMIUM(250)      |
| VIP(400)          |
+-------------------+
```

---

## The story in one paragraph

We needed a movie ticket booking system with screens, shows, seat types, and booking/cancellation. We split it clean: `Screen` generates the seat layout from row/column config. `Show` ties a movie to a screen at a time and tracks booked seats in a `HashSet`. `BookingService` orchestrates — it checks availability, locks seats, asks the `PriceCalculator` for the total, and creates a `Booking` record. Cancellation frees the seats and flips the status. Pricing is behind an interface so you can swap in weekend rates or student discounts without touching the booking logic. The demo showed booking regular, premium, and VIP seats, failing on a double-book, cancelling, and re-booking the freed seat.
