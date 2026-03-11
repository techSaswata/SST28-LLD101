# Ex8 — My Notes (ISP Fix: Student Club Admin Tools)

---

## What the problem asks to do

A student club has 3 roles:
- **Treasurer** — manages money (add income, add expense)
- **Secretary** — manages meeting notes (add minutes)
- **Event Lead** — manages events (create event, count events)

A `ClubConsole` uses all three roles to run club operations.

The problem says: **there's one big interface `ClubAdminTools` that has ALL operations** from all roles. Every tool implements all methods even if it doesn't use most. Fix it.

---

## The concept being used: ISP (Interface Segregation Principle)

Imagine you're a secretary. Your boss gives you a contract saying:
- "You must know how to write minutes" ✓
- "You must know how to manage budgets"
- "You must know how to plan events"

You only need the first one. But the contract forces you to pretend you can do all three. So you implement "manage budgets" as "do nothing" — which is a silent lie.

**ISP says:** A class should only be forced to implement methods it actually uses. Split the big interface into role-specific ones.

---

## What was broken (Before the fix)

`ClubAdminTools` had: `addIncome`, `addExpense`, `addMinutes`, `createEvent`, `getEventsCount`.

Each tool implemented all 5:
- `TreasurerTool` had `addMinutes`, `createEvent`, `getEventsCount` — all dummy no-ops
- `SecretaryTool` had `addIncome`, `addExpense`, `createEvent`, `getEventsCount` — all dummy
- `EventLeadTool` had `addIncome`, `addExpense`, `addMinutes` — all dummy

If `ClubConsole` accidentally called `treasurer.addMinutes(...)`, it would silently do nothing. No error. That's a hidden bug.

---

## Steps to identify and understand what to do

**Step 1 — Group methods by role**

- `addIncome`, `addExpense` → finance operations → `FinanceOps`
- `addMinutes` → minutes operations → `MinutesOps`
- `createEvent`, `getEventsCount` → events operations → `EventsOps`

**Step 2 — Create 3 small interfaces**

Each interface has only what that role needs.

**Step 3 — Each tool implements only its own interface**

- `TreasurerTool implements FinanceOps` — only addIncome and addExpense. No dummies.
- `SecretaryTool implements MinutesOps` — only addMinutes. No dummies.
- `EventLeadTool implements EventsOps` — only createEvent and getEventsCount. No dummies.

**Step 4 — ClubConsole uses the right interface per role**

```java
FinanceOps treasurer = new TreasurerTool(ledger);
MinutesOps secretary = new SecretaryTool(minutes);
EventsOps lead       = new EventLeadTool(events);
```

Now the compiler itself stops you from calling `treasurer.addMinutes(...)` — it's not even there.

---

## UML Diagram

```
   FinanceOps          MinutesOps           EventsOps
   (interface)         (interface)          (interface)
   addIncome()         addMinutes()         createEvent()
   addExpense()                             getEventsCount()
       ^                   ^                    ^
       |                   |                    |
  TreasurerTool       SecretaryTool        EventLeadTool
  (no dummies)        (no dummies)         (no dummies)

              +----------------------+
              |     ClubConsole      |
              |----------------------|
              | treasurer: FinanceOps|
              | secretary: MinutesOps|
              | lead: EventsOps      |
              |----------------------|
              | + run()              |
              +----------------------+
```

---

## The story in one paragraph

`ClubAdminTools` was one big interface covering finance, minutes, and events — so every tool had to pretend it could do everything. `TreasurerTool` had dummy `addMinutes()` and `createEvent()` that did nothing. We split the interface into three focused ones: `FinanceOps`, `MinutesOps`, and `EventsOps`. Each tool now implements only its own interface — no dummies, no silent failures. `ClubConsole` stores each tool as its specific interface type, so the compiler itself prevents accidentally calling finance methods on the secretary. That's ISP: each class is forced to implement only what it actually needs.
