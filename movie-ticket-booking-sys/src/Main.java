import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Movie Ticket Booking System ===\n");

        Movie movie1 = new Movie("M1", "Interstellar", 169);
        Movie movie2 = new Movie("M2", "The Dark Knight", 152);

        Screen screen1 = new Screen(1, 3, 2, 1, 8);
        Screen screen2 = new Screen(2, 4, 1, 1, 10);

        LocalDateTime now = LocalDateTime.of(2026, 3, 30, 14, 0);
        Show show1 = new Show("S1", movie1, screen1, now);
        Show show2 = new Show("S2", movie2, screen2, now.plusHours(3));

        BookingService service = new BookingService(new DefaultPriceCalculator());

        System.out.println("Shows:");
        System.out.println("  " + show1);
        System.out.println("  " + show2);

        System.out.println("\nAvailable seats for " + show1.getMovie().getTitle() + ": " + show1.getAvailableSeats().size());

        System.out.println("\n--- Booking 1: 2 regular seats ---");
        Booking b1 = service.book(show1, List.of("R1-C1", "R1-C2"));
        System.out.println(b1);

        System.out.println("\n--- Booking 2: 1 premium + 1 VIP seat ---");
        Booking b2 = service.book(show1, List.of("R4-C1", "R6-C1"));
        System.out.println(b2);

        System.out.println("\n--- Try booking already-booked seat ---");
        Booking b3 = service.book(show1, List.of("R1-C1"));
        System.out.println("Result: " + b3);

        System.out.println("\nAvailable seats now: " + show1.getAvailableSeats().size());

        System.out.println("\n--- Cancel Booking 1 ---");
        boolean cancelled = service.cancel(b1.getBookingId());
        System.out.println("Cancelled: " + cancelled);
        System.out.println("Booking status: " + b1.getStatus());

        System.out.println("\nAvailable seats after cancel: " + show1.getAvailableSeats().size());

        System.out.println("\n--- Re-book the freed seat ---");
        Booking b4 = service.book(show1, List.of("R1-C1"));
        System.out.println(b4);

        System.out.println("\n--- Booking on Show 2 ---");
        Booking b5 = service.book(show2, List.of("R1-C1", "R1-C2", "R5-C1", "R6-C1"));
        System.out.println(b5);
    }
}
