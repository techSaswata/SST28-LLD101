public class ExternalService {
    private final RateLimiter rateLimiter;

    public ExternalService(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public String callExternal(String key, String payload) {
        if (!rateLimiter.allowRequest(key)) {
            return "REJECTED: rate limit exceeded for " + key;
        }
        return "SUCCESS: external call with payload '" + payload + "' for " + key;
    }
}
