import java.util.List;

public interface PricingCalculator {
    InvoicePricing calculate(List<OrderLine> lines, MenuCatalog menu);
}
