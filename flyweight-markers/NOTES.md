# flyweight-markers — My Notes (Flyweight Pattern)

---

## What the problem asks to do

A CLI tool called **GeoDash** renders a large list of map markers (pins on a map). Each marker has:
- A **position**: `lat`, `lng`, `label` — unique per marker
- A **style**: `shape`, `color`, `size`, `filled` — only a few combinations exist (e.g. PIN|RED|12|F)

With 10,000 markers and only 20 possible styles, the old code was creating 10,000 separate `MarkerStyle` objects — one per marker — even when thousands of them had identical styles. That's wasteful memory.

Fix it so identical styles are **shared** — only as many `MarkerStyle` objects as there are distinct style combinations.

---

## The concept being used: Flyweight Pattern

Think of a book. Every copy of the same book has the same text (intrinsic). But each copy has its own owner, bookmarks, and annotations (extrinsic). You don't reprint the text for every owner — you share the same printed content.

**Flyweight Pattern says:** Split an object's state into:
- **Intrinsic state** — shared, repeated, immutable (the style: shape/color/size/filled)
- **Extrinsic state** — unique per instance, passed in when needed (lat/lng/label)

Store only one copy of each unique intrinsic state. All objects that need it point to the same shared instance.

---

## What was changed (From the commit diff)

### `MarkerStyle.java`

**Before:**
- Fields were not final (`private String shape`, etc.)
- Had **public setters** (`setShape`, `setColor`, `setSize`, `setFilled`) — mutable!
- Class was not `final`

**After:**
- All fields are `private final` — truly immutable
- All setters **removed**
- Class is `final` — can't be subclassed and accidentally made mutable
- Now it's a safe flyweight: you can share it everywhere without fear of it changing

### `MarkerStyleFactory.java`

**Before:**
```java
public MarkerStyle get(String shape, String color, int size, boolean filled) {
    String key = ...;
    // TODO: return cached instance if present; otherwise create, cache, and return.
    return new MarkerStyle(shape, color, size, filled);  // always creates new!
}
```
The cache existed but was never used — every call created a fresh object.

**After:**
```java
MarkerStyle style = cache.get(key);
if (style == null) {
    style = new MarkerStyle(shape, color, size, filled);
    cache.put(key, style);
}
return style;
```
Now: check the cache first. If found, return the existing shared instance. If not, create once, store, return. All future calls with the same key get the same object.

### `MapMarker.java`

**Before:**
```java
public MapMarker(double lat, double lng, String label,
                 String shape, String color, int size, boolean filled) {
    ...
    this.style = new MarkerStyle(shape, color, size, filled);  // creates per-marker!
}
```
Each `MapMarker` took raw style fields and created its own `MarkerStyle` internally. 10,000 markers = 10,000 `MarkerStyle` objects.

**After:**
```java
public MapMarker(double lat, double lng, String label, MarkerStyle style) {
    ...
    this.style = style;  // receives shared flyweight
}
```
Now takes an already-shared `MarkerStyle` from outside. The marker itself doesn't create style objects.

### `MapDataSource.java`

**Before:**
```java
out.add(new MapMarker(lat, lng, label, shape, color, size, filled));
```
Passing raw style data directly to `MapMarker` (which then wastefully created a new `MarkerStyle`).

**After:**
```java
private final MarkerStyleFactory styleFactory = new MarkerStyleFactory();
...
MarkerStyle style = styleFactory.get(shape, color, size, filled);
out.add(new MapMarker(lat, lng, label, style));
```
`MapDataSource` now uses the factory to get shared styles. The factory is created once and lives on the `MapDataSource`. Same style config → same `MarkerStyle` object handed to multiple markers.

---

## Steps to identify and understand what to do

**Step 1 — Identify what's intrinsic vs extrinsic**

- Style (shape, color, size, filled) → same for many markers → intrinsic, shareable
- Position (lat, lng, label) → unique per marker → extrinsic, not shared

**Step 2 — Make `MarkerStyle` immutable**

Remove all setters, make all fields `final`, make class `final`. Now it's safe to share — nobody can mutate the shared object.

**Step 3 — Implement the factory cache**

The factory has a `Map<String, MarkerStyle>`. Key = `"PIN|RED|12|F"` style. Check cache → hit: return existing. Miss: create, store, return.

**Step 4 — Change `MapMarker` to accept a `MarkerStyle`**

Instead of building its own style, the marker just holds a reference to the shared one passed in from outside.

**Step 5 — Use the factory in `MapDataSource`**

Create one `MarkerStyleFactory` on the data source. For each marker, call `styleFactory.get(...)` to get the shared style, then pass it to `new MapMarker(...)`.

**Result**: 10,000 markers might share only 20 `MarkerStyle` objects. `QuickCheck` can verify this by checking `factory.cacheSize()`.

---

## UML Diagram

```
              +---------------------+
              |    MarkerStyle      |  ← flyweight (immutable, shared)
              |---------------------|
              | - shape: final      |
              | - color: final      |
              | - size: final       |
              | - filled: final     |
              |---------------------|
              | getters only        |
              +---------------------+
                        ^
                        | creates & caches
              +---------------------+
              | MarkerStyleFactory  |
              |---------------------|
              | - cache: Map<String,|
              |   MarkerStyle>      |
              |---------------------|
              | get(shape,color,    |
              |   size,filled)      |  ← returns shared instance
              +---------------------+
                        ^
                        | uses
              +---------------------+
              |    MapDataSource    |
              |---------------------|
              | - styleFactory      |
              |---------------------|
              | loadMarkers(count)  |  ← gets shared style, creates MapMarker
              +---------------------+
                        |
                        | creates
                        v
              +---------------------+
              |     MapMarker       |
              |---------------------|
              | - lat               |  ← extrinsic (unique)
              | - lng               |  ← extrinsic (unique)
              | - label             |  ← extrinsic (unique)
              | - style: MarkerStyle|  ← shared flyweight reference
              +---------------------+
```

---

## The story in one paragraph

Every `MapMarker` was creating its own `new MarkerStyle(shape, color, size, filled)` internally — so 10,000 markers meant 10,000 style objects, even when thousands had identical styles. We split the state: style info (shape/color/size/filled) is **intrinsic** (shared), position info (lat/lng/label) is **extrinsic** (unique). `MarkerStyle` became fully immutable (all fields final, no setters) so it's safe to share. `MarkerStyleFactory` holds a cache — first time you ask for `PIN|RED|12|F`, it creates one and stores it; every future request for the same key returns the same object. `MapMarker` now receives a shared `MarkerStyle` instead of building its own. `MapDataSource` uses the factory. Result: 10,000 markers, only ~20 `MarkerStyle` objects. That's the Flyweight pattern.
