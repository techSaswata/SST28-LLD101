import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Show {
    private final String showId;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final Set<String> bookedSeatIds;

    public Show(String showId, Movie movie, Screen screen, LocalDateTime startTime) {
        this.showId = showId;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.bookedSeatIds = new HashSet<>();
    }

    public boolean isSeatAvailable(String seatId) {
        return !bookedSeatIds.contains(seatId);
    }

    public List<Seat> getAvailableSeats() {
        return screen.getSeats().stream()
                .filter(s -> !bookedSeatIds.contains(s.getSeatId()))
                .collect(Collectors.toList());
    }

    public boolean bookSeat(String seatId) {
        if (bookedSeatIds.contains(seatId)) return false;
        bookedSeatIds.add(seatId);
        return true;
    }

    public void cancelSeat(String seatId) {
        bookedSeatIds.remove(seatId);
    }

    public String getShowId() { return showId; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public LocalDateTime getStartTime() { return startTime; }

    public Seat findSeat(String seatId) {
        for (Seat s : screen.getSeats()) {
            if (s.getSeatId().equals(seatId)) return s;
        }
        return null;
    }

    @Override
    public String toString() {
        return showId + ": " + movie.getTitle() + " @ Screen " + screen.getScreenNumber() + " [" + startTime + "]";
    }
}
