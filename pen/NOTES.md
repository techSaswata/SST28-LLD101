# Pen — My Notes (LLD: Pen Design)

---

## What the problem asks to do

Design a Pen in Java with four operations:
1. `start()` — open the pen so it's ready to write
2. `write(content)` — write something using the pen
3. `close()` — close the pen
4. `refill(ink)` — replace the ink inside the pen

Different pen types (ball point, gel, fountain) write differently. Some pens can be refilled, some can't. Use all SOLID principles.

---

## The concepts being used

### SOLID Principles — all five

Think of a real pen shop:
- **SRP**: The pen itself just manages open/close state. It doesn't know *how* to write — that's the ink tip's job.
- **OCP**: Want a new pen type (marker, sketch pen)? Just create a new writing strategy. Don't touch `Pen.java`.
- **LSP**: `RefillablePen` extends `Pen`. Anywhere you use a `Pen`, you can swap in a `RefillablePen` — no surprises, no crashes.
- **ISP**: Not every pen can be refilled. So `Refillable` is a separate interface. A disposable ball pen doesn't need to pretend it can refill.
- **DIP**: `Pen` depends on the `WritingStrategy` interface, not on `GelStrategy` or `BallPointStrategy` directly. You inject the strategy from outside.

---

## Steps to identify, understand what to do, and what is exactly done to solve it

**Step 1 — Figure out the core responsibilities**

A pen has two different jobs: (1) managing its state (open/closed/has ink?) and (2) actually producing output. Those are two separate reasons to change — so they need to be separate.

We created `Pen.java` with fields `name`, `writingStrategy`, `ink`, and `open`. The `start()`, `write()`, and `close()` methods live here. The actual "how do I write on paper" logic is NOT here — it's delegated to `WritingStrategy`.

**Step 2 — Extract the writing behavior into a strategy interface**

Each pen type writes differently. Ball point pens write one way, gel pens another, fountain pens another. If we hard-code this inside `Pen`, we'd need `if-else` chains. Instead, we use the Strategy pattern.

We created `WritingStrategy` interface with `write(String content)` and `getType()`. Then three implementations: `BallPointStrategy`, `GelStrategy`, `FountainStrategy`. Each returns its own formatted output like `[Gel] Hello World`. `Pen` just calls `writingStrategy.write(content)` — it doesn't know or care which strategy it got.

**Step 3 — Separate refillable pens from non-refillable ones**

Not every pen can be refilled. A cheap ball pen you throw away when it's empty. A fountain pen you refill. If we put `refill()` on every `Pen`, the disposable pen would have a useless method — that violates ISP.

We created a `Refillable` interface with one method: `refill(Ink ink)`. Then `RefillablePen extends Pen implements Refillable`. Only pens that actually support refilling get this interface. A plain `Pen` doesn't have `refill()` at all.

**Step 4 — Model ink as its own class**

Ink has a color and a capacity. It's data that belongs together.

We created `Ink.java` with `final String color` and `final double capacity`. Both `Pen` and `RefillablePen` hold an `Ink` reference. When `RefillablePen.refill(newInk)` is called, it replaces the internal ink via the `protected setInk()` method.

**Step 5 — Wire it all together in Main**

We created a `RefillablePen` (gel) with blue ink, used it, refilled with red ink, used it again. Created a plain `Pen` (ball point) that can't be refilled. Created a `RefillablePen` (fountain). Also tested edge cases: a pen with no ink, and writing without calling `start()`.

---

## UML Diagram

```
        +------------------+
        |  WritingStrategy |  (interface)
        |------------------|
        | + write(content) |
        | + getType()      |
        +--------+---------+
                 |
    +------------+------------+
    |            |            |
+---+-------+ +--+--------+ +-+----------+
|BallPoint  | |GelStrategy| |Fountain    |
|Strategy   | |           | |Strategy    |
+-----------+ +-----------+ +------------+

        +------------------+
        |     Refillable   |  (interface)
        |------------------|
        | + refill(ink)    |
        +--------+---------+
                 |
                 |  implements
                 |
        +--------+---------+
        |  RefillablePen   |
        |------------------|         +-------+
        | + refill(ink)    |-------->|  Ink  |
        +--------+---------+         |-------|
                 |  extends          | color |
                 |                   | capacity|
        +--------+---------+         +-------+
        |       Pen        |
        |------------------|
        | - name           |
        | - writingStrategy|---> WritingStrategy
        | - ink            |---> Ink
        | - open           |
        |------------------|
        | + start()        |
        | + write(content) |
        | + close()        |
        +------------------+
```

---

## The story in one paragraph

We needed a pen that can start, write, close, and optionally refill. Instead of cramming everything into one class, we split it up. `Pen` manages state — is it open? does it have ink? The actual writing behavior is injected as a `WritingStrategy` (ball point, gel, fountain — each writes differently). Refilling is a separate `Refillable` interface — only pens that actually support it implement it, so disposable pens don't have a useless `refill()` method. `Ink` is its own little class with color and capacity. This gives us all five SOLID principles: SRP (pen vs strategy), OCP (new pen type = new strategy, no modification), LSP (RefillablePen works anywhere Pen works), ISP (Refillable is separate), DIP (Pen depends on WritingStrategy interface, not concretes).
