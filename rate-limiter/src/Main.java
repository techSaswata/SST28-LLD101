public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Rate Limiter Demo ===\n");

        RateLimiterConfig config = RateLimiterConfig.perMinute(5);
        System.out.println("Config: " + config);

        System.out.println("\n--- Fixed Window Counter ---");
        RateLimiter fixed = RateLimiterFactory.create(RateLimiterFactory.Algorithm.FIXED_WINDOW, config);
        ExternalService svc1 = new ExternalService(fixed);

        for (int i = 1; i <= 7; i++) {
            String result = svc1.callExternal("tenant-T1", "request-" + i);
            System.out.println("  " + i + ": " + result);
        }

        System.out.println("\n--- Sliding Window Counter ---");
        RateLimiter sliding = RateLimiterFactory.create(RateLimiterFactory.Algorithm.SLIDING_WINDOW, config);
        ExternalService svc2 = new ExternalService(sliding);

        for (int i = 1; i <= 7; i++) {
            String result = svc2.callExternal("tenant-T1", "request-" + i);
            System.out.println("  " + i + ": " + result);
        }

        System.out.println("\n--- Multiple keys (per-customer) ---");
        RateLimiter multi = RateLimiterFactory.create(RateLimiterFactory.Algorithm.FIXED_WINDOW, RateLimiterConfig.perMinute(3));
        ExternalService svc3 = new ExternalService(multi);

        for (int i = 1; i <= 4; i++) {
            System.out.println("  T1-" + i + ": " + svc3.callExternal("tenant-T1", "data"));
            System.out.println("  T2-" + i + ": " + svc3.callExternal("tenant-T2", "data"));
        }

        System.out.println("\n--- Switching algorithm without changing business logic ---");
        RateLimiter algo1 = RateLimiterFactory.create(RateLimiterFactory.Algorithm.FIXED_WINDOW, config);
        RateLimiter algo2 = RateLimiterFactory.create(RateLimiterFactory.Algorithm.SLIDING_WINDOW, config);

        ExternalService serviceA = new ExternalService(algo1);
        ExternalService serviceB = new ExternalService(algo2);

        System.out.println("  Fixed:   " + serviceA.callExternal("key1", "test"));
        System.out.println("  Sliding: " + serviceB.callExternal("key1", "test"));
        System.out.println("  (Same ExternalService code, different algorithm)");
    }
}
