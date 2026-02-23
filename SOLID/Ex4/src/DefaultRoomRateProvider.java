import java.util.HashMap;
import java.util.Map;

public class DefaultRoomRateProvider implements RoomRateProvider {
    private final Map<Integer, Money> rates;

    private DefaultRoomRateProvider(Map<Integer, Money> rates) {
        this.rates = rates;
    }

    public static DefaultRoomRateProvider create() {
        Map<Integer, Money> map = new HashMap<>();
        map.put(LegacyRoomTypes.SINGLE, new Money(14000.0));
        map.put(LegacyRoomTypes.DOUBLE, new Money(15000.0));
        map.put(LegacyRoomTypes.TRIPLE, new Money(12000.0));
        map.put(LegacyRoomTypes.DELUXE, new Money(16000.0));
        return new DefaultRoomRateProvider(map);
    }

    @Override
    public Money baseFor(int roomType) {
        Money rate = rates.get(roomType);
        if (rate != null) return rate;
        return rates.get(LegacyRoomTypes.DELUXE);
    }
}
