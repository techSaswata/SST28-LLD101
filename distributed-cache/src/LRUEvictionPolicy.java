import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final LinkedHashMap<K, Boolean> accessOrder;

    public LRUEvictionPolicy() {
        this.accessOrder = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public void keyAccessed(K key) {
        accessOrder.put(key, Boolean.TRUE);
    }

    @Override
    public K evict() {
        Iterator<K> it = accessOrder.keySet().iterator();
        if (it.hasNext()) {
            K oldest = it.next();
            it.remove();
            return oldest;
        }
        return null;
    }

    @Override
    public void keyRemoved(K key) {
        accessOrder.remove(key);
    }
}
