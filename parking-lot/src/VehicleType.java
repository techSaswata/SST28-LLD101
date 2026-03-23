import java.util.List;

public enum VehicleType {
    TWO_WHEELER(List.of(SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE)),
    CAR(List.of(SlotType.MEDIUM, SlotType.LARGE)),
    BUS(List.of(SlotType.LARGE));

    private final List<SlotType> compatibleSlots;

    VehicleType(List<SlotType> compatibleSlots) {
        this.compatibleSlots = compatibleSlots;
    }

    public List<SlotType> getCompatibleSlots() { return compatibleSlots; }
}
