import java.util.List;

public class DefaultPriceCalculator implements PriceCalculator {
    @Override
    public double calculate(List<Seat> seats) {
        double total = 0;
        for (Seat seat : seats) {
            total += seat.getType().getPrice();
        }
        return total;
    }
}
