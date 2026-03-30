import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingService {
    private final PriceCalculator priceCalculator;
    private final Map<String, Booking> bookings;
    private int bookingCounter;

    public BookingService(PriceCalculator priceCalculator) {
        this.priceCalculator = priceCalculator;
        this.bookings = new HashMap<>();
        this.bookingCounter = 0;
    }

    public Booking book(Show show, List<String> seatIds) {
        List<Seat> seats = new ArrayList<>();
        for (String seatId : seatIds) {
            if (!show.isSeatAvailable(seatId)) {
                System.out.println("Seat " + seatId + " is already booked.");
                return null;
            }
            Seat seat = show.findSeat(seatId);
            if (seat == null) {
                System.out.println("Seat " + seatId + " does not exist.");
                return null;
            }
            seats.add(seat);
        }

        for (String seatId : seatIds) {
            show.bookSeat(seatId);
        }

        double total = priceCalculator.calculate(seats);
        String bookingId = "BK-" + (++bookingCounter);
        Booking booking = new Booking(bookingId, show, seats, total);
        bookings.put(bookingId, booking);
        return booking;
    }

    public boolean cancel(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null || booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Invalid or already cancelled booking.");
            return false;
        }
        for (Seat seat : booking.getSeats()) {
            booking.getShow().cancelSeat(seat.getSeatId());
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return true;
    }

    public Booking getBooking(String bookingId) { return bookings.get(bookingId); }
}
