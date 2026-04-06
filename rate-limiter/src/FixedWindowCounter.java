import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowCounter implements RateLimiter {
    private final RateLimiterConfig config;
    private final ConcurrentHashMap<String, long[]> windows;

    public FixedWindowCounter(RateLimiterConfig config) {
        this.config = config;
        this.windows = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long windowStart = now - (now % config.getWindowSizeMillis());

        long[] entry = windows.get(key);
        if (entry == null || entry[0] != windowStart) {
            windows.put(key, new long[]{windowStart, 1});
            return true;
        }

        if (entry[1] < config.getMaxRequests()) {
            entry[1]++;
            return true;
        }
        return false;
    }
}
