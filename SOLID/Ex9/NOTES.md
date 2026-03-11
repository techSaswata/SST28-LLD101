# Ex9 — My Notes (DIP Fix: Assignment Evaluation Pipeline)

---

## What the problem asks to do

When a student submits an assignment, the system runs it through a pipeline:
1. Check for plagiarism
2. Grade the code against a rubric
3. Write a report
4. Print a final PASS/FAIL result

The problem says: **`EvaluationPipeline` directly creates `PlagiarismChecker`, `CodeGrader`, and `ReportWriter` using `new` inside the method**. Fix it so the pipeline doesn't depend on those specific classes.

---

## The concept being used: DIP (Dependency Inversion Principle)

Think of a laptop charger. Your laptop doesn't care if the electricity comes from a wall socket, a generator, or solar panels — it just needs something that gives the right voltage through the right port.

If your laptop only worked with one specific wall socket in your house, it would be useless everywhere else. That's the violation.

**DIP says:** High-level code (the pipeline) should depend on **abstractions** (interfaces), not on low-level concrete classes. The actual implementations can be swapped out.

---

## What was broken (Before the fix)

`EvaluationPipeline.evaluate` looked like:
```java
PlagiarismChecker pc = new PlagiarismChecker();
CodeGrader grader = new CodeGrader();
ReportWriter writer = new ReportWriter();
```

The pipeline is the "big boss" — it decides the evaluation flow. But it was directly creating and depending on specific workers. Problems:
- Want to test the pipeline with a fake grader? Can't — it always uses `CodeGrader`.
- Want to swap the plagiarism checker? Have to edit the pipeline.
- The pipeline knows too much about the low-level details.

---

## Steps to identify and understand what to do

**Step 1 — Identify the three "worker" roles**

- Someone checks plagiarism → `PlagiarismCheckerService` interface
- Someone grades the code → `CodeGraderService` interface
- Someone writes the report → `ReportWriterService` interface

**Step 2 — Create interfaces for each**

Each interface has just one method (the one the pipeline calls).

**Step 3 — Make the concrete classes implement those interfaces**

`PlagiarismChecker implements PlagiarismCheckerService`
`CodeGrader implements CodeGraderService`
`ReportWriter implements ReportWriterService`

The classes themselves don't change much — just add `implements`.

**Step 4 — Inject the dependencies via constructor**

`EvaluationPipeline` now takes the interfaces in its constructor and stores them as fields. No more `new` inside the method.

**Step 5 — Main wires everything together**

`Main` is the only place that calls `new PlagiarismChecker()`, `new CodeGrader()`, etc. It passes them to the pipeline constructor. This is called "wiring" — you assemble the pieces at the top, not inside the logic.

---

## UML Diagram

```
              +------------------------+
              |   EvaluationPipeline   |
              |------------------------|
              | - rubric               |
              | - plagChecker          |---> PlagiarismCheckerService (interface)
              | - grader               |---> CodeGraderService (interface)
              | - writer               |---> ReportWriterService (interface)
              |------------------------|
              | + evaluate(submission) |
              +------------------------+

PlagiarismCheckerService (interface)    CodeGraderService (interface)
        ^                                       ^
        |                                       |
  PlagiarismChecker                        CodeGrader

ReportWriterService (interface)
        ^
        |
   ReportWriter

              +------------------------+
              |         Main           |
              |------------------------|
              | creates all concretes  |
              | wires them into pipeline|
              +------------------------+
```

---

## The story in one paragraph

`EvaluationPipeline.evaluate` was doing `new PlagiarismChecker()`, `new CodeGrader()`, `new ReportWriter()` right inside the method — it was tightly glued to those specific classes. If you wanted to test with a fake grader, you couldn't. We created three interfaces: `PlagiarismCheckerService`, `CodeGraderService`, and `ReportWriterService`. The pipeline now takes these as constructor arguments and only talks to the interfaces. `Main` creates the real concrete objects and passes them in. The pipeline doesn't care what the actual implementation is. Swap the checker, change the report format — the pipeline stays untouched. That's DIP: high-level logic depends on abstractions, not on low-level details.
