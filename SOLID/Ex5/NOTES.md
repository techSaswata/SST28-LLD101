# Ex5 — My Notes (LSP Fix: File Exporter Hierarchy)

---

## What the problem asks to do

A reporting tool can export data in three formats: PDF, CSV, JSON.
All three extend a base class `Exporter` which says: call `export(request)` and get back an `ExportResult`.

The problem says: **the three child exporters are all misbehaving in different ways**. Fix them so all three can be used through the base class without surprises.

---

## The concept being used: LSP (Liskov Substitution Principle)

Imagine you order a "drink" at a canteen. It could be water, juice, or tea — but any of them should be drinkable. If one of them is actually gasoline disguised as a drink, that's a broken promise.

**LSP says:** If code works with the parent class (`Exporter`), it should work the same way with any child (`PdfExporter`, `CsvExporter`, `JsonExporter`) — no crashes, no surprises, no inconsistent behavior.

---

## What was broken (Before the fix)

### PdfExporter
- If the body was longer than 20 characters, it would **throw a crash** (`IllegalArgumentException`).
- The parent `Exporter` never said it can throw. So callers had to wrap it in `try-catch` just for PDF. That's not fair — the parent didn't warn about this.

### CsvExporter
- It was replacing `\n` with spaces and `,` with spaces. Fine — CSV format needs this.
- But it was also silently **dropping data** in some edge cases. The parent said "export the request", not "quietly mangle it".

### JsonExporter
- If you passed `null`, it returned **empty bytes** without any error flag.
- Other exporters might crash or do something different with null.
- The behavior was inconsistent — callers couldn't predict what they'd get.

### The real root problem
- `export()` returned `ExportResult` which had no "did it succeed?" field — no way to communicate failure without throwing.
- `null` input wasn't handled uniformly.

---

## Steps to identify and understand what to do

**Step 1 — See the ugly `try-catch` in Main**

`Main` had a `try-catch` specifically for the PDF exporter. That's the red flag — callers should not need to know which exporter might throw. The base contract should cover all cases.

We read `Main.java` and found a `try-catch(IllegalArgumentException)` block wrapping only the PDF export call. No other exporter needed that catch. That told us PdfExporter was violating LSP by throwing where the base contract didn't allow it.

**Step 2 — Fix ExportResult to carry success/failure**

Add `ok` (boolean) and `errorMessage` (String) to `ExportResult`. Now instead of throwing, exporters can return `ExportResult.error("reason")`. Clean, safe, no crash.

We added `boolean ok` and `String errorMessage` fields to `ExportResult`, along with two factory methods: `ExportResult.ok(contentType, bytes)` for success and `ExportResult.error(message)` for failure. All existing `return new ExportResult(...)` calls were updated to use the factory methods.

**Step 3 — Normalize null input in the base class**

Add a `normalize(req)` helper in `Exporter` (the base class). Every child calls this first — it converts null into empty strings. Now all three exporters handle null the same way.

We added a `protected ExportRequest normalize(ExportRequest req)` method to the `Exporter` base class. It returns an `ExportRequest` with empty strings replacing nulls. Each subclass calls `req = normalize(req)` at the start of their `export()` method.

**Step 4 — PdfExporter returns error instead of throwing**

Instead of `throw new IllegalArgumentException(...)`, it now returns `ExportResult.error("PDF cannot handle content > 20 chars")`.

We changed `PdfExporter.export()` to replace the `throw new IllegalArgumentException(...)` with `return ExportResult.error("PDF content too long")`. The exporter now always returns a result — never throws.

**Step 5 — Main becomes clean**

No more `try-catch`. Just check `if (out.ok)` — same check for all exporters. Substitutable.

We removed the try-catch from `Main` and replaced it with a simple `if (!result.ok)` check. The same check works for PDF, CSV, and JSON. The caller no longer needs to know anything about which exporter it's using.

---

## UML Diagram

```
              +----------------------+
              |       Exporter       |  (abstract base)
              |----------------------|
              | + export(req)        |  ← contract: always returns ExportResult, never throws
              | # normalize(req)     |  ← shared helper: handle null safely
              +----------------------+
                          ^
            ______________|_______________
           |              |              |
     PdfExporter    CsvExporter    JsonExporter
     (returns        (cleans        (normalizes
      error if        newlines/      null input,
      body > 20       commas)        returns ok)
      chars)

              +----------------------+
              |     ExportResult     |
              |----------------------|
              | ok: boolean          |
              | contentType: String  |
              | bytes: byte[]        |
              | errorMessage: String |
              |----------------------|
              | ok(contentType, b[]) |  ← factory: success
              | error(message)       |  ← factory: failure
              +----------------------+
```

---

## The story in one paragraph

Three exporters all extended `Exporter`, but each one had its own weird behavior — PDF crashed, JSON gave empty bytes silently, CSV mangled data. The parent's promise was unclear and the return type (`ExportResult`) had no way to say "it failed". We fixed two things: added `ok` and `errorMessage` to `ExportResult` so any exporter can signal failure without crashing, and added a `normalize()` helper in the base class so all exporters handle null the same way. Now PdfExporter returns an error result instead of throwing. Main just checks `if (out.ok)` — no try-catch, no format-specific workarounds. Any exporter can replace any other through the base class. That's LSP.
