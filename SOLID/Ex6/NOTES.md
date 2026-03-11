# Ex6 — My Notes (LSP Fix: Notification Sender)

---

## What the problem asks to do

We have a campus system that sends notifications — via Email, SMS, and WhatsApp.

There is one parent class `NotificationSender` and three children: `EmailSender`, `SmsSender`, `WhatsAppSender`.

The problem says: **these children are misbehaving**. Each one does something unexpected compared to what the parent promised. Fix them so any child can be used in place of the parent without surprises.

---

## The concept being used: LSP (Liskov Substitution Principle)

Think of it like this:

> If your mom says "any vehicle can take you to school", then a bicycle, a car, or a bus should all work.
> But if one of them suddenly explodes when you sit on it — that's a broken promise.

**LSP says:** A child class must keep the same promises as the parent. It should not demand extra things, throw unexpected errors, or silently change the meaning of things.

---

## What was broken (Before the fix)

### EmailSender
- It was **secretly cutting the message short** (truncating to 40 characters) without telling anyone.
- The parent never said "I will cut your message". So this is a broken promise.

### WhatsAppSender
- If the phone number did not start with `+`, it would **throw a crash** (`RuntimeException`).
- The parent's `send()` never said it could throw. So callers had to wrap it in `try-catch` just for WhatsApp — that's bad.

### SmsSender
- It was silently ignoring `subject`. Fine on its own, but the base contract was vague — nobody knew what fields would or wouldn't be used.

### The real root problem
- `send()` returned `void` — there was no way to know if it succeeded or failed.
- Validation logic was scattered inside each sender — each one had its own rules, decided on its own.

---

## Steps to identify, understand what to do, and what is exactly done to solve it

**Step 1 — Read Main.java and see the ugly part**

In `Main.java`, WhatsApp had a `try-catch` that no other sender needed. That's a red flag — it means the callers had to know which sender might crash. That breaks LSP.

`Main.java` had `try { wa.send(n); } catch (RuntimeException ex) { ... }` wrapped only around WhatsApp — `email.send(n)` and `sms.send(n)` were called plainly with no try-catch. That asymmetry was the smell. The try-catch block was removed entirely.

**Step 2 — Ask: "what is the base contract?"**

The base class `NotificationSender` said:
```
public abstract void send(Notification n);
```
It returns nothing. It might crash. It might silently do weird things. That's a vague and dangerous contract.

`NotificationSender.java` had `public abstract void send(Notification n)` — `void`, no return, no way to communicate failure. The constructor was also only `(AuditLog audit)` — no way to share a validator. Both of these were the root of all the mess in the children.

**Step 3 — Fix the contract first**

Change the return type from `void` to `SendResult`. Now every sender must return either "it worked" or "it failed with this reason". No crashes, no silent weirdness.

`NotificationSender`'s abstract method was changed to `public abstract SendResult send(Notification n)`. The constructor was changed to `(AuditLog audit, NotificationValidator validator)` and `validator` was stored as a `protected final` field so all children can use it. All three concrete senders updated their `@Override send()` to return `SendResult`.

**Step 4 — Create SendResult**

A simple class with two fields: `ok` (true/false) and `errorMessage` (the reason if it failed).

A new file `SendResult.java` was created with `final boolean ok` and `final String errorMessage`, a private constructor, and two static factory methods: `SendResult.ok()` returns success, `SendResult.error(String message)` returns failure. No setters — immutable once built.

**Step 5 — Pull validation out of each sender**

Create `NotificationValidator` — one class that knows the rules for each channel:
- Email needs a valid email address
- SMS needs a phone number
- WhatsApp needs a phone number that starts with `+`

Now each sender just calls the validator, gets a `SendResult` back, and returns it. No one throws. No one hides things.

A new file `NotificationValidator.java` was created with three methods: `validateEmail(n)`, `validatePhone(n)`, `validateWhatsAppPhone(n)` — each returning a `SendResult`. In `WhatsAppSender`, the old `throw new IllegalArgumentException(...)` was replaced with `return validator.validateWhatsAppPhone(n)`. In `EmailSender`, the silent `body.substring(0, 40)` truncation was removed — body is now passed through as-is after a null check.

**Step 6 — Update Main.java**

Instead of `try-catch`, just check `waResult.ok`. Clean, simple, no surprises.

In `Main.java`, a `NotificationValidator` was created and passed into all three sender constructors. The `try-catch` around `wa.send(n)` was deleted and replaced with `SendResult waResult = wa.send(n); if (!waResult.ok) { System.out.println("WA ERROR: " + waResult.errorMessage); }` — same behavior, but now any sender can communicate failure the exact same clean way.

---

## UML Diagram

```
         +----------------------+
         |  NotificationSender  |  (abstract)
         |----------------------|
         | - audit: AuditLog    |
         | - validator: NotificationValidator |
         |----------------------|
         | + send(n): SendResult|  ← base contract, must be honored
         +----------+-----------+
                    |
       +------------+-------------+
       |            |             |
+------+------+ +---+-----+ +-----+------+
| EmailSender | |SmsSender| |WhatsAppSender|
|-------------| |---------| |------------|
| send(n)     | | send(n) | |  send(n)   |
| → validate  | | → valid | |  → valid   |
| → print     | | → print | |  → print   |
| → ok()      | | → ok()  | |  → ok()    |
+-------------+ +---------+ +------------+
       |                          |
       +--------+   +-------------+
                |   |
       +--------+---+----------+
       | NotificationValidator |
       |-----------------------|
       | validateEmail(n)      |
       | validatePhone(n)      |
       | validateWhatsAppPhone |
       +-----------+-----------+
                   |
           returns SendResult

+------------------+
|    SendResult    |
|------------------|
| ok: boolean      |
| errorMessage: String |
|------------------|
| ok()             |  ← factory: success
| error(msg)       |  ← factory: failure
+------------------+
```

---

## The story in one paragraph

We had three senders that all extended the same parent — but each was doing its own weird thing behind the scenes. One was quietly chopping messages. One was throwing a crash. The parent's promise was too vague. So we fixed the promise first: `send()` now returns a `SendResult` instead of `void`, so you always know what happened. Then we moved all the "is this input valid?" logic into one place — `NotificationValidator`. Each sender now just asks the validator, gets a clean yes/no back, and returns it. No crashes. No silent cuts. No surprises. If you give any sender to someone who only knows the parent class, it will behave exactly as expected. That is LSP.
