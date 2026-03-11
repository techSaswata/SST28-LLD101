# immutable-tickets — My Notes (Immutable Classes + Builder Pattern)

---

## What the problem asks to do

A support tool called **HelpLite** creates incident tickets. A ticket has fields like `id`, `reporterEmail`, `title`, `priority`, `tags`, `assigneeEmail`, etc.

The problem says: `IncidentTicket` was **mutable** — it had public setters, multiple constructors, and `TicketService` was modifying tickets after creating them. This is dangerous because once a ticket is "created and logged", it should never change. Fix it by making the ticket **immutable** and using a **Builder**.

---

## The concept being used: Immutable Classes + Builder Pattern

**Immutable** means: once you create the object, nothing can change it. Like a printed receipt — once it's printed, you can't change the amount on it.

**Builder** means: instead of a messy constructor with 10 parameters, you build the object step by step:
```java
new IncidentTicket.Builder("TCK-1", "a@b.com", "Login broken")
    .priority("HIGH")
    .addTag("AUTH")
    .build();
```
Clean, readable, and the `build()` is the only place validation runs.

---

## What was changed (From the commit diff)

### `IncidentTicket.java`

**Before:**
- All fields were non-final (`private String id`, etc.)
- Had **public setters** for every field (`setId`, `setPriority`, `setTags`, etc.)
- Had **3 different constructors**
- `getTags()` returned the internal list directly — anyone could add to it from outside
- Validation was scattered (in `TicketService`, not in ticket itself)

**After:**
- All fields are `private final` — cannot change after construction
- Class itself is `final` — cannot be subclassed
- **No setters at all**
- `getTags()` returns the same list but it's `Collections.unmodifiableList(...)` — trying to add to it throws `UnsupportedOperationException`
- A **static inner class `Builder`** was added with all optional fields and a `build()` method that does all validation
- `toBuilder()` was added — copies all current values into a new Builder so you can "update" by creating a new ticket

### `TicketService.java`

**Before:**
- `createTicket()` called `new IncidentTicket(...)` and then mutated it: `t.setPriority(...)`, `t.setSource(...)`, etc.
- `assign()` and `escalateToCritical()` directly called setters on the existing ticket — **mutating it in place**
- `assign()` and `escalateToCritical()` returned `void`

**After:**
- `createTicket()` uses the Builder, calls `.build()` once — no mutation after
- `assign()` and `escalateToCritical()` now return a **new** `IncidentTicket` using `t.toBuilder()...build()`
- Original ticket is untouched — `t1` is still the same after calling `assign(t1, ...)`

### `TryIt.java`

Shows the before/after story. Before: mutations worked silently. After: `t1` stays unchanged when you assign or escalate, and trying to mutate the tags list throws an exception.

### `Validation.java`

Minor fix: regex pattern had `\s` (wrong in Java string) changed to `\\s` (correct Java escape).

---

## Steps to identify and understand what to do

**Step 1 — Make all fields final**

Remove setters. All fields get `private final`. Constructor becomes private (only Builder calls it).

**Step 2 — Defensive copy on tags**

In the constructor: `this.tags = Collections.unmodifiableList(new ArrayList<>(builder.tags))`.
- `new ArrayList<>(builder.tags)` — copies the list so the builder's list can't affect the ticket
- `Collections.unmodifiableList(...)` — wraps it so outside code can't add/remove via `getTags()`

**Step 3 — Create the Builder**

Required fields go in Builder's constructor. Optional fields have setter-style methods that return `this` (fluent). `build()` validates everything and calls `new IncidentTicket(this)`.

**Step 4 — Add `toBuilder()`**

For "updates", don't touch the old object. Instead: copy all values into a new Builder, change what you want, call `.build()`. Returns a brand new ticket.

**Step 5 — Fix TicketService**

Replace all `t.setX(...)` calls with `t.toBuilder().x(...).build()`. Methods that used to return `void` now return the new `IncidentTicket`.

---

## UML Diagram

```
              +-----------------------------+
              |      IncidentTicket         |  ← final class, all final fields
              |-----------------------------|
              | - id: final String          |
              | - reporterEmail: final String|
              | - title: final String       |
              | - priority: final String    |
              | - tags: final List (unmod.) |
              | - ...more final fields      |
              |-----------------------------|
              | + getters only (no setters) |
              | + toBuilder()               |  ← returns Builder pre-filled with current values
              |-----------------------------|
              |   static class Builder      |
              |   - id, email, title        |  ← required in constructor
              |   - priority, tags, ...     |  ← optional, set via fluent methods
              |   + build()                 |  ← validates EVERYTHING, returns new ticket
              +-----------------------------+

              +-----------------------------+
              |       TicketService         |
              |-----------------------------|
              | + createTicket(...)         |  ← uses Builder, returns immutable ticket
              | + assign(t, email)          |  ← returns NEW ticket via toBuilder()
              | + escalateToCritical(t)     |  ← returns NEW ticket via toBuilder()
              +-----------------------------+
```

---

## The story in one paragraph

`IncidentTicket` had public setters, 3 constructors, and its tags list leaked outside — you could mutate a ticket long after it was "saved". `TicketService` was calling `t.setPriority(...)` and `t.getTags().add(...)` after creation, making the audit trail unreliable. We made `IncidentTicket` fully immutable: all fields `final`, no setters, tags wrapped in an unmodifiable list, and the class itself `final`. A `Builder` inner class handles step-by-step construction and runs all validation in one place at `.build()`. To "update" a ticket — use `toBuilder()` to get a pre-filled Builder, change what you need, and `.build()` gives you a brand new ticket. The original is untouched. That's the immutable pattern.
