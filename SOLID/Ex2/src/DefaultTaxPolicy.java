public class DefaultTaxPolicy implements TaxPolicy {
    @Override
    public double taxPercent(String customerType) {
        return TaxRules.taxPercent(customerType);
    }
}
