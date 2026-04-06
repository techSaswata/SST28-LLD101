public class ModuloDistribution implements DistributionStrategy {
    @Override
    public int getNodeIndex(String key, int nodeCount) {
        return Math.abs(key.hashCode()) % nodeCount;
    }
}
