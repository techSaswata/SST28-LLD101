# Ex1 — My Notes (SRP Fix: Student Onboarding)

---

## What the problem asks to do

A new student joins the campus. We need to:
1. Take a raw string like `name=Riya;email=riya@sst.edu;phone=9876543210;program=CSE`
2. Check if the input is valid
3. Generate a student ID like `SST-2026-0001`
4. Save the student to the database
5. Print a confirmation message

The problem says: **all 5 of these jobs are crammed inside one single method** in `OnboardingService`. Fix that.

---

## The concept being used: SRP (Single Responsibility Principle)

Think of a school office:
- One person reads the form
- Another person checks if it's filled correctly
- Another person gives the student an ID card
- Another person files it in a cabinet
- Another person prints the welcome slip

If **one person does all of this**, it becomes a mess. If one rule changes (say, the ID format changes), you have to dig into a big pile of work to find that one thing.

**SRP says:** Each class should do only one job. If it has multiple reasons to change, split it.

---

## What was broken (Before the fix)

Inside `OnboardingService.registerFromRawInput`, all this was happening in one method:
- Splitting the raw string by `=` and `;` (parsing)
- Checking if name/email/phone/program are valid (validation)
- Making the student ID using `IdUtil` (ID generation)
- Saving directly to `FakeDb` (persistence)
- Printing "OK", "ERROR", "CONFIRMATION" directly (output)

**The problem:** If tomorrow you want to change the output format, you'd have to touch the same method that also has validation logic. That's risky. One bug and everything breaks.

---

## Steps to identify and understand what to do

**Step 1 — Read the big messy method**

Open `OnboardingService.registerFromRawInput`. Count how many different things it does. You'll see: parsing, validating, ID-making, saving, printing — all in one place.

**Step 2 — Give each job its own class**

- Parsing → `StudentInputParser` (reads raw string, returns a clean `StudentInput` object)
- Validation → `StudentValidator` (checks the fields, returns a list of errors)
- ID Generation → `StudentIdGenerator` interface + `DefaultStudentIdGenerator`
- Saving → `StudentRepository` interface (so `OnboardingService` doesn't have to know about `FakeDb`)
- Printing → `OnboardingPrinter` (prints the result to console)

**Step 3 — Create a result object**

`RegistrationResult` holds everything about what happened — was it a success? what errors? what record? — so the printer can just read this object and print. The service doesn't print anything itself.

**Step 4 — Clean up `OnboardingService`**

Now `OnboardingService` just coordinates: call parser → call validator → call ID generator → call repo → return result. Done. It doesn't know how to print or how the DB works.

---

## UML Diagram

```
                  +--------------------+
                  |  OnboardingService |
                  |--------------------|
                  | - repo             |---> StudentRepository (interface)
                  | - parser           |---> StudentInputParser
                  | - validator        |---> StudentValidator
                  | - idGenerator      |---> StudentIdGenerator (interface)
                  |--------------------|
                  | + registerFromRaw()|
                  |   returns          |
                  |   RegistrationResult|
                  +--------------------+
                           |
                           v
                  +--------------------+
                  |  RegistrationResult|
                  |--------------------|
                  | rawInput           |
                  | errors             |
                  | record             |
                  | totalCount         |
                  |--------------------|
                  | success(...)       |
                  | failure(...)       |
                  | isSuccess()        |
                  +--------------------+
                           |
                  read by  v
                  +--------------------+
                  |  OnboardingPrinter |
                  |--------------------|
                  | + print(result)    |  ← only job: print stuff
                  +--------------------+

StudentRepository (interface)
        ^
        |
     FakeDb  ← the actual in-memory store

StudentIdGenerator (interface)
        ^
        |
  DefaultStudentIdGenerator
```

---

## The story in one paragraph

`OnboardingService` was like a one-man army — it was parsing, validating, making IDs, saving, and printing all by itself. We split its work into specialized helpers. `StudentInputParser` reads the raw string. `StudentValidator` checks the fields. `StudentIdGenerator` makes the ID. `StudentRepository` saves the record. `OnboardingPrinter` prints the result. `OnboardingService` now just calls them in order and passes the result along. If you want to change the print format tomorrow — you only touch `OnboardingPrinter`. That's SRP.
