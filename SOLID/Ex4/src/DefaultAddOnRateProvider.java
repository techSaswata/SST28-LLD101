import java.util.EnumMap;
import java.util.Map;

public class DefaultAddOnRateProvider implements AddOnRateProvider {
    private final Map<AddOn, Money> fees;

    private DefaultAddOnRateProvider(Map<AddOn, Money> fees) {
        this.fees = fees;
    }

    public static DefaultAddOnRateProvider create() {
        Map<AddOn, Money> map = new EnumMap<>(AddOn.class);
        map.put(AddOn.MESS, new Money(1000.0));
        map.put(AddOn.LAUNDRY, new Money(500.0));
        map.put(AddOn.GYM, new Money(300.0));
        return new DefaultAddOnRateProvider(map);
    }

    @Override
    public Money feeFor(AddOn addOn) {
        Money fee = fees.get(addOn);
        return fee == null ? new Money(0.0) : fee;
    }
}
