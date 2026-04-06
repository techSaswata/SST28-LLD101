public interface EvictionPolicy<K> {
    void keyAccessed(K key);
    K evict();
    void keyRemoved(K key);
}
