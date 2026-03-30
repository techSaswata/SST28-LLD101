import java.util.List;

public class Booking {
    private final String bookingId;
    private final Show show;
    private final List<Seat> seats;
    private final double totalPrice;
    private BookingStatus status;

    public Booking(String bookingId, Show show, List<Seat> seats, double totalPrice) {
        this.bookingId = bookingId;
        this.show = show;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getBookingId() { return bookingId; }
    public Show getShow() { return show; }
    public List<Seat> getSeats() { return seats; }
    public double getTotalPrice() { return totalPrice; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Booking[" + bookingId + " | " + show.getMovie().getTitle()
                + " | seats=" + seats + " | ₹" + totalPrice + " | " + status + "]";
    }
}
