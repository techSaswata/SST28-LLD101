public class Main {
    public static void main(String[] args) {
        System.out.println("=== Elevator System Demo ===\n");

        ElevatorController controller = new ElevatorController(3, new NearestElevatorStrategy());
        controller.status();

        System.out.println("\n--- Requests from different floors ---");
        Elevator e1 = controller.handleRequest(new Request(5, Direction.UP));
        controller.handleInternalRequest(e1, 8);

        Elevator e2 = controller.handleRequest(new Request(3, Direction.DOWN));
        controller.handleInternalRequest(e2, 1);

        Elevator e3 = controller.handleRequest(new Request(7, Direction.UP));
        controller.handleInternalRequest(e3, 10);

        System.out.println("\n--- Processing all stops ---");
        controller.step();

        System.out.println();
        controller.status();

        System.out.println("\n--- New round of requests ---");
        Elevator e4 = controller.handleRequest(new Request(2, Direction.UP));
        controller.handleInternalRequest(e4, 6);

        Elevator e5 = controller.handleRequest(new Request(9, Direction.DOWN));
        controller.handleInternalRequest(e5, 4);

        System.out.println("\n--- Processing ---");
        controller.step();

        System.out.println();
        controller.status();
    }
}
