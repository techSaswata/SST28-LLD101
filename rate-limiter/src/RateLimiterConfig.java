public class RateLimiterConfig {
    private final int maxRequests;
    private final long windowSizeMillis;

    public RateLimiterConfig(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
    }

    public int getMaxRequests() { return maxRequests; }
    public long getWindowSizeMillis() { return windowSizeMillis; }

    public static RateLimiterConfig perMinute(int max) {
        return new RateLimiterConfig(max, 60_000);
    }

    public static RateLimiterConfig perHour(int max) {
        return new RateLimiterConfig(max, 3_600_000);
    }

    @Override
    public String toString() {
        return maxRequests + " per " + (windowSizeMillis / 1000) + "s";
    }
}
