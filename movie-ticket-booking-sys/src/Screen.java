import java.util.ArrayList;
import java.util.List;

public class Screen {
    private final int screenNumber;
    private final List<Seat> seats;

    public Screen(int screenNumber, int regularRows, int premiumRows, int vipRows, int cols) {
        this.screenNumber = screenNumber;
        this.seats = new ArrayList<>();
        int row = 1;
        for (int r = 0; r < regularRows; r++, row++) {
            for (int c = 1; c <= cols; c++) {
                seats.add(new Seat("R" + row + "-C" + c, row, c, SeatType.REGULAR));
            }
        }
        for (int r = 0; r < premiumRows; r++, row++) {
            for (int c = 1; c <= cols; c++) {
                seats.add(new Seat("R" + row + "-C" + c, row, c, SeatType.PREMIUM));
            }
        }
        for (int r = 0; r < vipRows; r++, row++) {
            for (int c = 1; c <= cols; c++) {
                seats.add(new Seat("R" + row + "-C" + c, row, c, SeatType.VIP));
            }
        }
    }

    public int getScreenNumber() { return screenNumber; }
    public List<Seat> getSeats() { return seats; }

    @Override
    public String toString() { return "Screen " + screenNumber + " (" + seats.size() + " seats)"; }
}
