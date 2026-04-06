import java.util.ArrayList;
import java.util.List;

public class DistributedCache {
    private final List<CacheNode<String, String>> nodes;
    private final DistributionStrategy strategy;
    private final Database database;

    public DistributedCache(int nodeCount, int capacityPerNode, DistributionStrategy strategy, Database database) {
        this.strategy = strategy;
        this.database = database;
        this.nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            nodes.add(new CacheNode<>("Node-" + i, capacityPerNode, new LRUEvictionPolicy<>()));
        }
    }

    public String get(String key) {
        int idx = strategy.getNodeIndex(key, nodes.size());
        CacheNode<String, String> node = nodes.get(idx);

        String value = node.get(key);
        if (value != null) {
            System.out.println("  [CACHE HIT]  " + node.getNodeId() + " → " + key + "=" + value);
            return value;
        }

        String dbValue = database.get(key);
        if (dbValue != null) {
            System.out.println("  [CACHE MISS] " + node.getNodeId() + " → fetched " + key + " from DB");
            node.put(key, dbValue);
            return dbValue;
        }

        System.out.println("  [NOT FOUND]  " + key + " not in cache or DB");
        return null;
    }

    public void put(String key, String value) {
        int idx = strategy.getNodeIndex(key, nodes.size());
        CacheNode<String, String> node = nodes.get(idx);
        node.put(key, value);
        database.put(key, value);
        System.out.println("  [PUT] " + node.getNodeId() + " → " + key + "=" + value);
    }

    public void status() {
        System.out.println("--- Cache Status ---");
        for (CacheNode<String, String> node : nodes) {
            System.out.println("  " + node);
        }
    }
}
