import java.util.List;

public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator select(List<Elevator> elevators, Request request) {
        Elevator best = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int distance = Math.abs(e.getCurrentFloor() - request.getFloor());

            if (e.isIdle()) {
                if (distance < minDistance) {
                    minDistance = distance;
                    best = e;
                }
            } else if (e.getDirection() == request.getDirection()) {
                boolean onTheWay = (request.getDirection() == Direction.UP && e.getCurrentFloor() <= request.getFloor())
                        || (request.getDirection() == Direction.DOWN && e.getCurrentFloor() >= request.getFloor());
                if (onTheWay && distance < minDistance) {
                    minDistance = distance;
                    best = e;
                }
            }
        }

        if (best == null) {
            int min = Integer.MAX_VALUE;
            for (Elevator e : elevators) {
                int d = Math.abs(e.getCurrentFloor() - request.getFloor());
                if (d < min) {
                    min = d;
                    best = e;
                }
            }
        }
        return best;
    }
}
