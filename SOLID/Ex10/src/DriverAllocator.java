public class DriverAllocator implements DriverAllocatorService {
    public String allocate(String studentId) {
        // fake deterministic driver
        return "DRV-17";
    }
}
