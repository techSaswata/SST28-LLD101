import java.util.ArrayList;
import java.util.List;

public class DefaultPricingCalculator implements PricingCalculator {
    @Override
    public InvoicePricing calculate(List<OrderLine> lines, MenuCatalog menu) {
        double subtotal = 0.0;
        List<InvoiceLine> priced = new ArrayList<>();
        for (OrderLine l : lines) {
            MenuItem item = menu.get(l.itemId);
            double lineTotal = item.price * l.qty;
            subtotal += lineTotal;
            priced.add(new InvoiceLine(item.name, l.qty, lineTotal));
        }
        return new InvoicePricing(priced, subtotal, lines.size());
    }
}
