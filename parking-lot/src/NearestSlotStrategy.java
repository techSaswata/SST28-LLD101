import java.util.List;

public class NearestSlotStrategy implements SlotAssignmentStrategy {

    @Override
    public ParkingSlot findSlot(List<ParkingSlot> slots, VehicleType vehicleType, int entryGateId) {
        List<SlotType> compatible = vehicleType.getCompatibleSlots();
        ParkingSlot nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied() && compatible.contains(slot.getType())) {
                int distance = Math.abs(slot.getDistanceFromGate() - entryGateId);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = slot;
                }
            }
        }
        return nearest;
    }
}
