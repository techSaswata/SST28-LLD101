package com.example.metrics;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global metrics registry — a proper, thread-safe, lazy-initialized Singleton.
 *
 * Thread safety   : guaranteed by the static-holder idiom (JLS §12.4.2).
 * Reflection guard: constructor throws if an instance already exists.
 * Serialization   : readResolve() returns the singleton.
 */
public class MetricsRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Flag used to detect reflection-based second construction
    private static volatile boolean instanceCreated = false;

    private final Map<String, Long> counters = new HashMap<>();

    // ── Static holder idiom (lazy + thread-safe) ────────────────────────
    private static class Holder {
        private static final MetricsRegistry INSTANCE = new MetricsRegistry();
    }

    // ── Private constructor with reflection guard ───────────────────────
    private MetricsRegistry() {
        if (instanceCreated) {
            throw new IllegalStateException(
                    "MetricsRegistry is a singleton — use getInstance(). "
                    + "Reflection-based construction is not allowed.");
        }
        instanceCreated = true;
    }

    public static MetricsRegistry getInstance() {
        return Holder.INSTANCE;
    }

    // ── Serialization guard ─────────────────────────────────────────────
    @Serial
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }

    // ── Counter operations (unchanged) ──────────────────────────────────

    public synchronized void setCount(String key, long value) {
        counters.put(key, value);
    }

    public synchronized void increment(String key) {
        counters.put(key, getCount(key) + 1);
    }

    public synchronized long getCount(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public synchronized Map<String, Long> getAll() {
        return Collections.unmodifiableMap(new HashMap<>(counters));
    }
}
