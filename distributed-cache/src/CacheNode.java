import java.util.HashMap;
import java.util.Map;

public class CacheNode<K, V> {
    private final String nodeId;
    private final int capacity;
    private final Map<K, V> store;
    private final EvictionPolicy<K> evictionPolicy;

    public CacheNode(String nodeId, int capacity, EvictionPolicy<K> evictionPolicy) {
        this.nodeId = nodeId;
        this.capacity = capacity;
        this.store = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
    }

    public V get(K key) {
        if (store.containsKey(key)) {
            evictionPolicy.keyAccessed(key);
            return store.get(key);
        }
        return null;
    }

    public void put(K key, V value) {
        if (store.containsKey(key)) {
            store.put(key, value);
            evictionPolicy.keyAccessed(key);
            return;
        }
        if (store.size() >= capacity) {
            K evicted = evictionPolicy.evict();
            if (evicted != null) {
                store.remove(evicted);
                System.out.println("  [" + nodeId + "] Evicted key: " + evicted);
            }
        }
        store.put(key, value);
        evictionPolicy.keyAccessed(key);
    }

    public boolean containsKey(K key) { return store.containsKey(key); }
    public String getNodeId() { return nodeId; }
    public int size() { return store.size(); }

    @Override
    public String toString() { return nodeId + "(size=" + store.size() + "/" + capacity + ")"; }
}
