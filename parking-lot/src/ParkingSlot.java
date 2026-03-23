public class ParkingSlot {
    private final int slotNumber;
    private final SlotType type;
    private final int floor;
    private final int distanceFromGate;
    private boolean occupied;

    public ParkingSlot(int slotNumber, SlotType type, int floor, int distanceFromGate) {
        this.slotNumber = slotNumber;
        this.type = type;
        this.floor = floor;
        this.distanceFromGate = distanceFromGate;
        this.occupied = false;
    }

    public int getSlotNumber() { return slotNumber; }
    public SlotType getType() { return type; }
    public int getFloor() { return floor; }
    public int getDistanceFromGate() { return distanceFromGate; }
    public boolean isOccupied() { return occupied; }

    public void occupy() { occupied = true; }
    public void free() { occupied = false; }

    @Override
    public String toString() {
        return "Slot#" + slotNumber + "(" + type + ",F" + floor + ")";
    }
}
