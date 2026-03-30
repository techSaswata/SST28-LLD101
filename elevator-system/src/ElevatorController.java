import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElevatorController {
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy strategy;

    public ElevatorController(int numElevators, ElevatorSelectionStrategy strategy) {
        this.strategy = strategy;
        this.elevators = new ArrayList<>();
        for (int i = 1; i <= numElevators; i++) {
            elevators.add(new Elevator(i));
        }
    }

    public Elevator handleRequest(Request request) {
        Elevator selected = strategy.select(elevators, request);
        selected.addStop(request.getFloor());
        System.out.println("Assigned " + request + " → " + selected);
        return selected;
    }

    public void handleInternalRequest(Elevator elevator, int destinationFloor) {
        elevator.addStop(destinationFloor);
        System.out.println("Internal: Elevator " + elevator.getId() + " → floor " + destinationFloor);
    }

    public void step() {
        for (Elevator e : elevators) {
            e.processAllStops();
        }
    }

    public List<Elevator> getElevators() { return Collections.unmodifiableList(elevators); }

    public void status() {
        System.out.println("--- Elevator Status ---");
        for (Elevator e : elevators) {
            System.out.println("  " + e);
        }
    }
}
