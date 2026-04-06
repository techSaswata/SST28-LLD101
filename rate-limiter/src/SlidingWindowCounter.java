import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindowCounter implements RateLimiter {
    private final RateLimiterConfig config;
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> logs;

    public SlidingWindowCounter(RateLimiterConfig config) {
        this.config = config;
        this.logs = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long windowStart = now - config.getWindowSizeMillis();

        ConcurrentLinkedDeque<Long> timestamps = logs.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        while (!timestamps.isEmpty() && timestamps.peekFirst() <= windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < config.getMaxRequests()) {
            timestamps.addLast(now);
            return true;
        }
        return false;
    }
}
