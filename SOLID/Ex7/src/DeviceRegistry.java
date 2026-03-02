public class DeviceRegistry {
    private final java.util.List<Object> devices = new java.util.ArrayList<>();

    public void add(Object d) { devices.add(d); }

    public <T> T getFirst(Class<T> type) {
        for (Object d : devices) {
            if (type.isInstance(d)) return type.cast(d);
        }
        throw new IllegalStateException("Missing device for: " + type.getSimpleName());
    }
}
