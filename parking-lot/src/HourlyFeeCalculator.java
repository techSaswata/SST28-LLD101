import java.util.HashMap;
import java.util.Map;

public class HourlyFeeCalculator implements FeeCalculator {
    private final Map<SlotType, Double> hourlyRates;

    public HourlyFeeCalculator() {
        hourlyRates = new HashMap<>();
        hourlyRates.put(SlotType.SMALL, 10.0);
        hourlyRates.put(SlotType.MEDIUM, 20.0);
        hourlyRates.put(SlotType.LARGE, 40.0);
    }

    @Override
    public double calculate(SlotType slotType, long hours) {
        long billableHours = Math.max(1, hours);
        return hourlyRates.getOrDefault(slotType, 20.0) * billableHours;
    }
}
