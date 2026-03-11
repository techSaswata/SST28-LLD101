# singleton-metrics — My Notes (Singleton Pattern)

---

## What the problem asks to do

A CLI tool called **PulseMeter** has a `MetricsRegistry` that stores counters like `REQUESTS_TOTAL`, `DB_ERRORS`, etc. Any part of the app should be able to increment or read these counters. There should only ever be **one** instance of this registry — that's the whole point.

The starter code was **intentionally broken** in three ways:
1. The constructor was `public` — anyone could do `new MetricsRegistry()` and get a separate, unrelated registry
2. `getInstance()` was racy — two threads could both pass the `if (INSTANCE == null)` check at the same time and create two instances
3. Serialization could create a brand new instance from `readResolve`

`MetricsLoader` was also broken — it did `new MetricsRegistry()` instead of using the singleton.

Fix all of this.

---

## The concept being used: Singleton Pattern

Think of a school principal's office. There's only one principal. Everyone who needs to talk to the principal goes to the same office. You don't create a new principal every time.

**Singleton says:** Only one instance of this class should ever exist in the whole application. Anyone who asks for it gets the same one.

---

## What was changed (From the commit diff)

### `MetricsRegistry.java`

**Before:**
- Constructor was `public` — completely open to abuse
- `getInstance()` had a racy check: `if (INSTANCE == null) INSTANCE = new MetricsRegistry()` — not thread-safe
- No reflection guard (someone could call the constructor via reflection to bypass `getInstance()`)
- `readResolve()` was missing (a `TODO` comment)

**After:**
- **Constructor is now `private`** and checks a `static volatile boolean instanceCreated` flag — if someone tries to call it a second time (via reflection), it throws `IllegalStateException`
- **Static Holder Idiom**: instead of the racy `getInstance()`, a private static inner class `Holder` holds the single instance. Java guarantees this is loaded exactly once, thread-safely, when first accessed
- **`readResolve()` added** — returns `getInstance()` so deserialization hands back the same singleton, not a new object

### `MetricsLoader.java`

**Before:**
```java
MetricsRegistry registry = new MetricsRegistry(); // BROKEN
```

**After:**
```java
MetricsRegistry registry = MetricsRegistry.getInstance(); // correct
```

That's the only change — one line. But it's the critical one.

---

## Steps to identify and understand what to do

**Step 1 — Spot the constructor visibility**

If the constructor is `public`, anyone can make `new MetricsRegistry()`. Make it `private`.

We opened `MetricsRegistry.java` and saw `public MetricsRegistry()` — completely exposed. We changed it to `private MetricsRegistry()`. That alone would block `new MetricsRegistry()` from compiling anywhere outside the class.

**Step 2 — Fix thread safety with the Static Holder idiom**

Instead of checking `if (INSTANCE == null)` yourself (which is racy), let the JVM do it for you:
```java
private static class Holder {
    static final MetricsRegistry INSTANCE = new MetricsRegistry();
}
public static MetricsRegistry getInstance() {
    return Holder.INSTANCE;
}
```
The inner class `Holder` is only loaded when `getInstance()` is first called. The JVM guarantees class loading is thread-safe. No locks needed.

We deleted the old `getInstance()` that had the racy `if (INSTANCE == null)` check. We created a private static inner class `Holder` with `static final MetricsRegistry INSTANCE = new MetricsRegistry()`. We rewrote `getInstance()` to just return `Holder.INSTANCE`. The JVM handles thread safety for free during class loading.

**Step 3 — Block reflection**

Add a `static volatile boolean instanceCreated` flag. In the constructor, check it — if already `true`, throw. This stops someone from doing `Constructor.setAccessible(true)` and calling it anyway.

We added `private static volatile boolean instanceCreated = false;` to the class. In the private constructor, we added: if `instanceCreated` is true, throw `IllegalStateException`; otherwise set it to true. Now even reflection cannot create a second instance.

**Step 4 — Block serialization**

Add `readResolve()` that returns `getInstance()`. When Java deserializes the object, it calls this method instead of using the deserialized one.

We added `private Object readResolve() { return getInstance(); }` to the class. There was a TODO comment marking exactly where this should go — we filled it in.

**Step 5 — Fix MetricsLoader**

Change `new MetricsRegistry()` to `MetricsRegistry.getInstance()`. Done.

We opened `MetricsLoader.java`, found `MetricsRegistry registry = new MetricsRegistry();` — which now wouldn't compile anyway since the constructor is private. We replaced it with `MetricsRegistry registry = MetricsRegistry.getInstance();`.

---

## UML Diagram

```
              +-----------------------------+
              |      MetricsRegistry        |
              |-----------------------------|
              | - instanceCreated: boolean  |  ← static flag for reflection guard
              | - counters: Map<String,Long>|
              |-----------------------------|
              | - MetricsRegistry()         |  ← private, throws if called twice
              | + getInstance()             |  ← returns Holder.INSTANCE
              | + increment(key)            |
              | + getCount(key)             |
              | + getAll()                  |
              | - readResolve()             |  ← returns getInstance() on deserialization
              |-----------------------------|
              |  private static class Holder|
              |    INSTANCE = new ...()     |  ← loaded once by JVM, thread-safe
              +-----------------------------+
                        ^
                        | uses getInstance()
              +-----------------------------+
              |      MetricsLoader          |
              |-----------------------------|
              | + loadFromFile(path)        |  ← reads props, sets counts on singleton
              +-----------------------------+
```

---

## The story in one paragraph

`MetricsRegistry` was supposed to be a singleton but had a public constructor, a racy `getInstance()`, no reflection guard, and no serialization protection — basically anyone could create as many instances as they wanted. We fixed it with the **Static Holder idiom**: a private inner class `Holder` holds the one true instance, and Java's class-loading guarantee makes it thread-safe for free. The constructor was made private and now throws if called a second time (blocks reflection attacks). `readResolve()` was added to make deserialization return the same instance. And `MetricsLoader` was fixed to call `getInstance()` instead of `new`. Now there's truly one registry everywhere.
