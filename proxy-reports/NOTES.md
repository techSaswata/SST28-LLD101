# proxy-reports — My Notes (Proxy Pattern)

---

## What the problem asks to do

A tool called **CampusVault** opens internal reports for different users. Reports have a classification: `PUBLIC`, `FACULTY`, or `ADMIN`.

The starter code had `ReportViewer` talking directly to `ReportFile` (concrete class). Every time someone opened a report, it would load the file immediately — no access check, no caching. Anyone could read anything.

Fix it so:
1. Unauthorized users get blocked
2. The expensive file load only happens when access is granted (lazy loading)
3. If the same report is opened again, it doesn't reload from disk (caching)

---

## The concept being used: Proxy Pattern

Think of a security guard at a building entrance. The actual office (real subject) is inside. The guard (proxy) stands at the door:
- Checks your ID → if you're not allowed, you don't get in
- Only unlocks the door once you're verified → lazy access
- After the first time, remembers you — doesn't ask for ID every visit

**Proxy Pattern says:** Put a middleman (proxy) in front of the real object. The proxy and the real object implement the same interface. Callers talk to the proxy — they don't even know there's a real object behind it.

---

## What was changed (From the commit diff)

### `ReportProxy.java`

**Before:**
```java
// Placeholder — always creates and loads the real report directly
RealReport report = new RealReport(reportId, title, classification);
report.display(user);
```
No access check. Every `display()` call created a fresh `RealReport` (no caching, no lazy loading — just broken).

**After:**
- Added `private RealReport cachedReport;` field — starts as `null`
- `display()` now does 3 things in order:
  1. **Access check**: calls `accessControl.canAccess(user, classification)` — if denied, prints `[ACCESS DENIED]` and returns immediately
  2. **Lazy load**: only if access is granted AND `cachedReport == null`, creates `new RealReport(...)`
  3. **Delegates**: calls `cachedReport.display(user)` — the real work happens here

The `RealReport` is only ever created on the first authorized access. Subsequent calls reuse the cached one.

### `RealReport.java`

**Before:**
```java
public void display(User user) {
    System.out.println("TODO: implement via real loading");
}
```
Was just a TODO placeholder.

**After:**
- Added `private String content;` field — starts `null`
- `display()` checks if `content == null` — if so, calls `loadFromDisk()` (which simulates a 120ms delay)
- Subsequent calls skip the disk load since `content` is already populated
- Prints the full report details and content

### `ReportViewer.java`

**Before:**
```java
public void open(ReportFile report, User user) { ... }
```
Was hardwired to `ReportFile` (concrete class).

**After:**
```java
public void open(Report report, User user) { ... }
```
Now depends on the `Report` interface. `ReportFile`, `RealReport`, `ReportProxy` — any of them can be passed in.

### `App.java`

**Before:**
```java
ReportFile publicReport = new ReportFile("R-101", "Orientation Plan", "PUBLIC");
```
Creating raw `ReportFile` objects directly.

**After:**
```java
Report publicReport = new ReportProxy("R-101", "Orientation Plan", "PUBLIC");
```
All three reports are now `ReportProxy` objects held as `Report` interface references. The demo shows: student denied on FACULTY report, faculty loads it (disk load happens), admin loads ADMIN report, admin opens same ADMIN report again (no disk load this time — cached).

---

## Steps to identify and understand what to do

**Step 1 — Create the `Report` interface**

Both the proxy and the real report need to implement the same interface so the viewer can use either.
```java
interface Report { void display(User user); }
```

**Step 2 — Build `RealReport` as the actual worker**

`RealReport` does the slow disk load. It caches the content internally (`private String content`) so it only loads once even if `display()` is called multiple times.

**Step 3 — Build `ReportProxy` as the gatekeeper**

The proxy holds the report metadata (id, title, classification) and a nullable `RealReport cachedReport`.
- On `display(user)`: check access first. If denied — stop.
- If allowed and `cachedReport == null` — create the `RealReport` now (lazy).
- Delegate to `cachedReport.display(user)`.

**Step 4 — Update `ReportViewer` to use the interface**

Change `ReportFile` → `Report` in the parameter. Now it works with proxies.

**Step 5 — Wire it in `App`**

Replace `new ReportFile(...)` with `new ReportProxy(...)`. Hold them as `Report` interface type.

---

## UML Diagram

```
              +------------------+
              |     Report       |  (interface)
              |------------------|
              | display(user)    |
              +------------------+
                       ^
              _________|_________
             |                   |
      +-------------+     +--------------+
      | ReportProxy |     |  RealReport  |
      |-------------|     |--------------|
      | - reportId  |     | - reportId   |
      | - title     |     | - title      |
      | - classif.  |     | - classif.   |
      | - cached    |---->| - content    |  ← null until loaded
      |   Report    |     |              |
      |-------------|     |--------------|
      | display(u)  |     | display(u)   |  ← does the real work
      |  1.check    |     | loadFromDisk |  ← slow, called once
      |  2.lazyload |     +--------------+
      |  3.delegate |
      +-------------+
              ^
              | uses
      +-------------+
      | ReportViewer|
      |-------------|
      | open(Report,|
      |       User) |
      +-------------+
              ^
              | creates
      +-------------+
      |    App      |
      |-------------|
      | Report r1 = |
      |  new Proxy()|
      +-------------+
```

---

## The story in one paragraph

`ReportViewer` was directly using `ReportFile` — no access control, no lazy loading, a fresh file load on every call. We introduced a `Report` interface, made `RealReport` the actual file loader, and put `ReportProxy` in front of it. The proxy checks access first — if you're not allowed, it stops right there. If you are allowed, it lazily creates the `RealReport` only on the first access and caches it. Repeated calls reuse the cached report — no disk load. `ReportViewer` now talks to the `Report` interface, so it works with any proxy or real report. `App` creates `ReportProxy` objects and the whole system is now secure and efficient. That's the Proxy pattern.
