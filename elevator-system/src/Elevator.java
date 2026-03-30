import java.util.TreeSet;

public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private final TreeSet<Integer> upStops;
    private final TreeSet<Integer> downStops;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.upStops = new TreeSet<>();
        this.downStops = new TreeSet<>();
    }

    public void addStop(int floor) {
        if (floor > currentFloor) {
            upStops.add(floor);
        } else if (floor < currentFloor) {
            downStops.add(floor);
        }
    }

    public void move() {
        if (direction == Direction.UP) {
            if (!upStops.isEmpty()) {
                currentFloor = upStops.pollFirst();
                System.out.println("  Elevator " + id + " stopped at floor " + currentFloor);
            }
            if (upStops.isEmpty()) {
                direction = downStops.isEmpty() ? Direction.IDLE : Direction.DOWN;
            }
        } else if (direction == Direction.DOWN) {
            if (!downStops.isEmpty()) {
                currentFloor = downStops.pollLast();
                System.out.println("  Elevator " + id + " stopped at floor " + currentFloor);
            }
            if (downStops.isEmpty()) {
                direction = upStops.isEmpty() ? Direction.IDLE : Direction.UP;
            }
        }
    }

    public void processAllStops() {
        if (upStops.isEmpty() && downStops.isEmpty()) return;
        if (direction == Direction.IDLE) {
            direction = upStops.isEmpty() ? Direction.DOWN : Direction.UP;
        }
        while (!upStops.isEmpty() || !downStops.isEmpty()) {
            move();
        }
        direction = Direction.IDLE;
    }

    public int getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
    public boolean isIdle() { return direction == Direction.IDLE; }
    public int getPendingStops() { return upStops.size() + downStops.size(); }

    @Override
    public String toString() {
        return "Elevator " + id + " [floor=" + currentFloor + ", dir=" + direction + ", stops=" + getPendingStops() + "]";
    }
}
