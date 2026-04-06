public class Main {
    public static void main(String[] args) {
        System.out.println("=== Distributed Cache Demo ===\n");

        Database db = new Database();
        db.put("user:1", "Alice");
        db.put("user:2", "Bob");
        db.put("user:3", "Charlie");
        db.put("user:4", "Diana");
        db.put("user:5", "Eve");

        DistributedCache cache = new DistributedCache(3, 2, new ModuloDistribution(), db);

        System.out.println("--- Initial puts ---");
        cache.put("user:1", "Alice");
        cache.put("user:2", "Bob");
        cache.put("user:3", "Charlie");

        System.out.println();
        cache.status();

        System.out.println("\n--- Cache hits ---");
        cache.get("user:1");
        cache.get("user:2");

        System.out.println("\n--- Cache miss (fetches from DB) ---");
        cache.get("user:4");
        cache.get("user:5");

        System.out.println();
        cache.status();

        System.out.println("\n--- Eviction demo (node capacity = 2) ---");
        cache.put("user:6", "Frank");
        cache.put("user:7", "Grace");
        cache.put("user:8", "Hank");

        System.out.println();
        cache.status();

        System.out.println("\n--- Not found ---");
        cache.get("user:999");

        System.out.println("\n--- Re-fetch evicted key from DB ---");
        cache.get("user:1");
    }
}
