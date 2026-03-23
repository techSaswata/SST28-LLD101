import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<ParkingSlot> slots = new ArrayList<>();
        int id = 1;
        for (int i = 0; i < 3; i++) slots.add(new ParkingSlot(id++, SlotType.SMALL, 1, i + 1));
        for (int i = 0; i < 3; i++) slots.add(new ParkingSlot(id++, SlotType.MEDIUM, 1, i + 1));
        for (int i = 0; i < 2; i++) slots.add(new ParkingSlot(id++, SlotType.LARGE, 1, i + 1));
        for (int i = 0; i < 2; i++) slots.add(new ParkingSlot(id++, SlotType.SMALL, 2, i + 5));
        for (int i = 0; i < 2; i++) slots.add(new ParkingSlot(id++, SlotType.MEDIUM, 2, i + 5));
        for (int i = 0; i < 2; i++) slots.add(new ParkingSlot(id++, SlotType.LARGE, 2, i + 5));

        ParkingLot lot = new ParkingLot(slots, new NearestSlotStrategy(), new HourlyFeeCalculator());

        System.out.println("=== Parking Lot Demo ===\n");
        printStatus(lot);

        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 10, 0);

        Vehicle bike = new Vehicle("KA-01-1234", VehicleType.TWO_WHEELER);
        ParkingTicket t1 = lot.park(bike, now, 1);
        System.out.println("Parked: " + t1);

        Vehicle car = new Vehicle("KA-02-5678", VehicleType.CAR);
        ParkingTicket t2 = lot.park(car, now, 2);
        System.out.println("Parked: " + t2);

        Vehicle bus = new Vehicle("KA-03-9999", VehicleType.BUS);
        ParkingTicket t3 = lot.park(bus, now, 1);
        System.out.println("Parked: " + t3);

        lot.park(new Vehicle("KA-04-A", VehicleType.TWO_WHEELER), now, 1);
        lot.park(new Vehicle("KA-04-B", VehicleType.TWO_WHEELER), now, 1);
        lot.park(new Vehicle("KA-04-C", VehicleType.TWO_WHEELER), now, 1);
        lot.park(new Vehicle("KA-04-D", VehicleType.TWO_WHEELER), now, 1);
        ParkingTicket t4 = lot.park(new Vehicle("KA-04-E", VehicleType.TWO_WHEELER), now, 1);
        System.out.println("Bike in larger slot: " + t4);

        System.out.println();
        printStatus(lot);

        LocalDateTime exitTime = now.plusHours(3);
        double fee1 = lot.exit(t1, exitTime);
        System.out.println("\nExit " + t1.getVehicle() + " after 3h → Fee: ₹" + fee1);

        double fee2 = lot.exit(t2, exitTime);
        System.out.println("Exit " + t2.getVehicle() + " after 3h → Fee: ₹" + fee2);

        double fee3 = lot.exit(t3, exitTime);
        System.out.println("Exit " + t3.getVehicle() + " after 3h → Fee: ₹" + fee3);

        double fee4 = lot.exit(t4, exitTime);
        System.out.println("Exit bike-in-medium after 3h → Fee: ₹" + fee4 + " (medium rate!)");

        System.out.println();
        printStatus(lot);
    }

    private static void printStatus(ParkingLot lot) {
        System.out.println("--- Status ---");
        Map<SlotType, int[]> status = lot.status();
        for (SlotType st : SlotType.values()) {
            int[] counts = status.get(st);
            System.out.println(st + ": " + counts[1] + "/" + counts[0] + " available");
        }
        System.out.println();
    }
}
