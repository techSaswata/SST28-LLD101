import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {
    private final List<ParkingSlot> slots;
    private final SlotAssignmentStrategy assignmentStrategy;
    private final FeeCalculator feeCalculator;
    private final Map<String, ParkingTicket> activeTickets;
    private int ticketCounter;

    public ParkingLot(List<ParkingSlot> slots, SlotAssignmentStrategy assignmentStrategy, FeeCalculator feeCalculator) {
        this.slots = slots;
        this.assignmentStrategy = assignmentStrategy;
        this.feeCalculator = feeCalculator;
        this.activeTickets = new HashMap<>();
        this.ticketCounter = 0;
    }

    public ParkingTicket park(Vehicle vehicle, LocalDateTime entryTime, int entryGateId) {
        ParkingSlot slot = assignmentStrategy.findSlot(slots, vehicle.getType(), entryGateId);
        if (slot == null) {
            System.out.println("No available slot for " + vehicle);
            return null;
        }
        slot.occupy();
        String ticketId = "T-" + (++ticketCounter);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, slot, entryTime);
        activeTickets.put(ticketId, ticket);
        return ticket;
    }

    public double exit(ParkingTicket ticket, LocalDateTime exitTime) {
        if (ticket == null || !activeTickets.containsKey(ticket.getTicketId())) {
            System.out.println("Invalid ticket.");
            return -1;
        }
        long hours = Duration.between(ticket.getEntryTime(), exitTime).toHours();
        double fee = feeCalculator.calculate(ticket.getSlot().getType(), hours);
        ticket.getSlot().free();
        activeTickets.remove(ticket.getTicketId());
        return fee;
    }

    public Map<SlotType, int[]> status() {
        Map<SlotType, int[]> result = new HashMap<>();
        for (SlotType st : SlotType.values()) {
            result.put(st, new int[]{0, 0});
        }
        for (ParkingSlot slot : slots) {
            int[] counts = result.get(slot.getType());
            counts[0]++;
            if (!slot.isOccupied()) counts[1]++;
        }
        return result;
    }
}
