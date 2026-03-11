# Ex4 — My Notes (OCP Fix: Hostel Fee Calculator)

---

## What the problem asks to do

Students book hostel rooms. The fee depends on:
- The **room type** (SINGLE, DOUBLE, TRIPLE, DELUXE — each has a different monthly price)
- The **add-ons** they select (MESS +1000, LAUNDRY +500, GYM +300)

There's also a fixed deposit of 5000.

The problem says: **the calculator has a big `switch` for room types and a big `if/else` for add-ons**. Adding a new room type or add-on means editing that method. Fix it.

---

## The concept being used: OCP (Open/Closed Principle)

Think of a vending machine:
- It has slots for items (Chips = ₹20, Water = ₹15, Juice = ₹30)
- If you add a new item (Chocolate = ₹40), you just **put it in a new slot**
- You don't rebuild the whole machine

**OCP says:** When you want to add new behavior (new room type, new add-on), you should be able to do it **without editing the existing calculation logic**.

---

## What was broken (Before the fix)

`HostelFeeCalculator.calculateMonthly` looked like:
```java
switch (req.roomType) {
    case SINGLE  -> base = 14000;
    case DOUBLE  -> base = 15000;
    case TRIPLE  -> base = 12000;
    default      -> base = 16000;
}

for (AddOn a : req.addOns) {
    if (a == MESS)    add += 1000;
    else if (a == LAUNDRY) add += 500;
    else if (a == GYM)     add += 300;
}
```

Want to add a new room type "PENTHOUSE"? Edit the switch. New add-on "WIFI"? Edit the if/else. Every addition risks breaking existing cases.

---

## Steps to identify and understand what to do

**Step 1 — Identify the two separate "tables" of prices**

There are two kinds of pricing decisions:
1. Room base price (depends on room type)
2. Add-on price (depends on add-on type)

These are just lookup tables. They don't need to be hardcoded in the calculator.

We read `HostelFeeCalculator.calculateMonthly` and spotted two separate hardcoded tables: a `switch` for room type prices and an `if/else` chain for add-on prices. Both were just key→value lookups pretending to be logic.

**Step 2 — Create interfaces for the two lookups**

- `RoomRateProvider` → `Money baseFor(int roomType)`
- `AddOnRateProvider` → `Money feeFor(AddOn addOn)`

We created `RoomRateProvider` and `AddOnRateProvider` interfaces. The calculator would now depend only on these, not on any concrete implementation.

**Step 3 — Create default implementations using maps**

`DefaultRoomRateProvider` stores room type → price in a `HashMap`. No switch needed.
`DefaultAddOnRateProvider` stores add-on → price in an `EnumMap`. No if/else needed.

We created `DefaultRoomRateProvider` using a `HashMap<Integer, Money>` pre-populated with all room type prices. We created `DefaultAddOnRateProvider` using an `EnumMap<AddOn, Money>`. The switch and if/else chains were deleted entirely.

**Step 4 — Inject them into `HostelFeeCalculator`**

The calculator now takes these providers as constructor arguments. The calculation loop becomes:

```java
Money base = roomRates.baseFor(req.roomType);
Money add = Money(0);
for (AddOn a : req.addOns) {
    add = add.plus(addOnRates.feeFor(a));
}
```

We changed `HostelFeeCalculator`'s constructor to accept `RoomRateProvider` and `AddOnRateProvider`. The `calculateMonthly` method now calls `roomRates.baseFor(req.roomType)` and loops over add-ons calling `addOnRates.feeFor(a)`. No conditions in the calculator.

**Step 5 — Adding new room/add-on in future**

Just add a new entry in `DefaultRoomRateProvider` or `DefaultAddOnRateProvider`. The calculator code stays unchanged.

We confirmed this by tracing through `App` — it creates `DefaultRoomRateProvider` and `DefaultAddOnRateProvider`, passes them to the calculator. To add "PENTHOUSE" room type, only `DefaultRoomRateProvider`'s map needs one new entry.

---

## UML Diagram

```
              +------------------------+
              |  HostelFeeCalculator   |
              |------------------------|
              | - repo                 |
              | - roomRates            |---> RoomRateProvider (interface)
              | - addOnRates           |---> AddOnRateProvider (interface)
              |------------------------|
              | + process(req)         |
              | - calculateMonthly(req)|  ← no switch, no if/else
              +------------------------+

RoomRateProvider (interface)            AddOnRateProvider (interface)
        ^                                       ^
        |                                       |
DefaultRoomRateProvider              DefaultAddOnRateProvider
  (HashMap: SINGLE→14000,             (EnumMap: MESS→1000,
   DOUBLE→15000, etc.)                 LAUNDRY→500, GYM→300)
```

---

## The story in one paragraph

`HostelFeeCalculator` had a big `switch` for room types and a big `if/else` for add-ons. Every time a new room or add-on was introduced, someone had to edit the calculator and risk breaking things. We pulled the prices out into two provider classes: `DefaultRoomRateProvider` (a map of room type → price) and `DefaultAddOnRateProvider` (a map of add-on → price). The calculator just asks these providers for prices and adds them up. No switch, no if/else. To add a new room type tomorrow — just add one entry to the map. The calculator itself never changes. That's OCP.
