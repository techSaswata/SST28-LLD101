# Ex3 — My Notes (OCP Fix: Placement Eligibility Rules Engine)

---

## What the problem asks to do

We need to check if a student is eligible for placements. The rules are:
- No disciplinary flag
- CGR must be at least 8.0
- Attendance must be at least 75%
- Earned credits must be at least 20

If any rule fails, the student is NOT_ELIGIBLE and we show why.

The problem says: **all these rules are written as a big `if/else` chain inside one method**. Fix it so adding a new rule doesn't require editing that method.

---

## The concept being used: OCP (Open/Closed Principle)

Imagine a security checklist at the airport:
- Check passport ✓
- Check boarding pass ✓
- Check baggage ✓

If tomorrow they add "check vaccine certificate", you just **add a new item** to the checklist. You don't **rewrite** the existing checklist.

**OCP says:** A class should be open for extension (you can add new behavior) but closed for modification (you don't edit existing code to do it).

---

## What was broken (Before the fix)

Inside `EligibilityEngine.evaluate`, the code looked like this:
```
if (disciplinaryFlag) → NOT_ELIGIBLE
else if (cgr < 8.0)   → NOT_ELIGIBLE
else if (attendance < 75) → NOT_ELIGIBLE
else if (credits < 20) → NOT_ELIGIBLE
```

This is one big chain. If you want to add a new rule like "GPA must be above 7", you have to **go inside this method and add another else-if**. Every time you touch existing code, you risk breaking something. That's the violation.

---

## Steps to identify and understand what to do

**Step 1 — Notice the pattern**

Each rule does the same thing: look at the student profile → if something is wrong → return a reason string → else return null (meaning "pass").

**Step 2 — Create an interface for that pattern**

```java
interface EligibilityRule {
    String failureReason(StudentProfile s);
}
```

Every rule is now one class that implements this.

**Step 3 — Create one class per rule**

- `DisciplinaryFlagRule` → checks if the flag is set
- `CgrRule` → checks if CGR is below minimum
- `AttendanceRule` → checks attendance
- `CreditsRule` → checks credits

Each class is small, focused, and independent.

**Step 4 — Replace the if/else chain with a loop**

`EligibilityEngine` now holds a list of rules. It loops through them, calls `failureReason()`, and stops at the first failure.

```java
for (EligibilityRule rule : rules) {
    String reason = rule.failureReason(s);
    if (reason != null) { ... break; }
}
```

**Step 5 — Adding a new rule in the future**

Just create a new class `NewRule implements EligibilityRule` and add it to the list. You never touch `EligibilityEngine` again.

---

## UML Diagram

```
              +------------------------+
              |   EligibilityEngine    |
              |------------------------|
              | - rules: List<EligibilityRule> |
              |------------------------|
              | + evaluate(s)          |  ← loops through rules
              +------------------------+
                          |
                          | uses
                          v
              +------------------------+
              |   EligibilityRule      |  (interface)
              |------------------------|
              | + failureReason(s)     |  ← returns null if pass, reason if fail
              +------------------------+
                          ^
             _____________|_____________
            |         |        |        |
   DisciplinaryFlagRule  CgrRule  AttendanceRule  CreditsRule
   (checks flag)  (cgr<8.0) (att<75)  (credits<20)
```

---

## The story in one paragraph

`EligibilityEngine.evaluate` had a long chain of `if/else` — one block per rule. Every time a new placement rule came in, someone had to edit this method. We broke each rule into its own tiny class: `CgrRule`, `AttendanceRule`, `CreditsRule`, `DisciplinaryFlagRule`, all implementing the `EligibilityRule` interface. The engine now just loops over a list of rules. To add a new rule tomorrow — write a new class, add it to the list. You never touch the engine again. That's OCP: open for adding new rules, closed for modifying the evaluation logic.
