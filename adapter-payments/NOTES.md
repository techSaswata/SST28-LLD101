# adapter-payments — My Notes (Adapter Pattern)

---

## What the problem asks to do

An `OrderService` needs to charge customers. There are two payment SDKs available:
- **FastPayClient** — has a method `payNow(customerId, amountCents)` → returns a transaction ID
- **SafeCashClient** — works differently: you call `createPayment(amountCents, customerId)` which gives you a `SafeCashPayment` object, and then you call `.confirm()` on it to get the transaction ID

The problem says: `OrderService` was directly dealing with both SDKs and had a `switch` on the provider name. Fix it so `OrderService` only talks to one clean interface and doesn't know about the SDKs at all.

---

## The concept being used: Adapter Pattern

Think of a power socket. Your laptop charger has a 3-pin plug. A country has 2-pin sockets. You use an adapter — it has a 2-pin side that fits the socket, and a 3-pin side that fits your charger. Neither the wall socket nor your charger changed. The adapter translates between them.

**Adapter Pattern says:** When two incompatible interfaces need to work together, create an adapter that wraps one side and presents the interface the other side expects.

---

## What was changed (From the commit diff)

### `FastPayAdapter.java` — new file

Wraps `FastPayClient`. Implements `PaymentGateway`.
```java
public String charge(String customerId, int amountCents) {
    return client.payNow(customerId, amountCents);  // delegates to SDK
}
```
One line — just calls the SDK's method with the right arguments.

### `SafeCashAdapter.java` — new file

Wraps `SafeCashClient`. Implements `PaymentGateway`.
```java
public String charge(String customerId, int amountCents) {
    SafeCashPayment payment = client.createPayment(amountCents, customerId);
    return payment.confirm();  // two-step SDK call, hidden from OrderService
}
```
The two-step SafeCash flow is hidden inside the adapter. `OrderService` never sees it.

### `App.java`

**Before:**
```java
// TODO: register adapters instead of raw SDKs
// gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
// gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));
```
Both lines were commented out (TODOs).

**After:**
```java
gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));
```
Uncommented — adapters are now wired into the registry map and passed to `OrderService`.

### `OrderService.java` (was already refactored in the starter)

`OrderService` already took a `Map<String, PaymentGateway>` and called `gw.charge(customerId, amountCents)`. The student's job was just to implement the two adapters and wire them. The service itself only sees `PaymentGateway` — it has no idea FastPay or SafeCash exist.

---

## Steps to identify and understand what to do

**Step 1 — Look at the two SDKs**

`FastPayClient.payNow(customerId, amountCents)` — simple, one call.
`SafeCashClient.createPayment(amountCents, customerId)` + `.confirm()` — two calls, different argument order.

These don't match the `PaymentGateway` interface: `charge(customerId, amountCents)`.

**Step 2 — Create an adapter for each SDK**

Each adapter:
- Takes the SDK client in its constructor
- Implements `PaymentGateway`
- In `charge()`, calls the SDK's method(s) and returns the transaction ID

**Step 3 — Register in the map in App**

`App` creates the SDK clients, wraps them in adapters, and puts them in the map keyed by provider name.

**Step 4 — OrderService stays clean**

`OrderService.charge("fastpay", ...)` → looks up `"fastpay"` in the map → gets back a `PaymentGateway` → calls `.charge()`. It doesn't know if it's FastPay or SafeCash behind it.

To add a third payment provider tomorrow — write one new adapter class and add one line to the map in `App`. `OrderService` never changes.

---

## UML Diagram

```
              +------------------+
              |  PaymentGateway  |  (interface — the "target")
              |------------------|
              | charge(cid, amt) |
              +------------------+
                       ^
              _________|_________
             |                   |
   +------------------+   +-------------------+
   |  FastPayAdapter  |   |  SafeCashAdapter   |
   |------------------|   |-------------------|
   | - client         |   | - client           |
   |   :FastPayClient |   |   :SafeCashClient  |
   |------------------|   |-------------------|
   | charge(cid, amt) |   | charge(cid, amt)   |
   |  client.payNow() |   |  client.create()   |
   |                  |   |  + payment.confirm()|
   +------------------+   +-------------------+

              +------------------+
              |   OrderService   |
              |------------------|
              | - gateways:      |
              |   Map<String,    |
              |   PaymentGateway>|
              |------------------|
              | charge(provider, |
              |   cid, amt)      |  ← only talks to PaymentGateway
              +------------------+
                       ^
                       | wired by
              +------------------+
              |      App         |
              |------------------|
              | gateways.put(    |
              |  "fastpay",      |
              |  new FastPayAdapter(new FastPayClient()))  |
              | gateways.put(    |
              |  "safecash",     |
              |  new SafeCashAdapter(new SafeCashClient()))|
              +------------------+
```

---

## The story in one paragraph

`OrderService` needed to charge customers but had two SDKs with completely different APIs — `FastPayClient.payNow()` vs `SafeCashClient.createPayment().confirm()`. Instead of making `OrderService` know about both SDKs, we created two adapters. `FastPayAdapter` wraps `FastPayClient` and translates `charge()` → `payNow()`. `SafeCashAdapter` wraps `SafeCashClient` and translates `charge()` → the two-step create+confirm. Both adapters implement the same `PaymentGateway` interface. `OrderService` only ever calls `gateway.charge()` — it has no idea what SDK is behind it. `App` wires the adapters into a map and passes it in. Adding a third provider tomorrow? One new adapter class, one map entry. That's the Adapter pattern.
