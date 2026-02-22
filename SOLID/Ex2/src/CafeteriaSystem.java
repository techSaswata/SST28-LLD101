import java.util.List;

public class CafeteriaSystem {
    private final MenuCatalog menu;
    private final PricingCalculator pricingCalculator;
    private final TaxPolicy taxPolicy;
    private final DiscountPolicy discountPolicy;
    private final InvoiceFormatter formatter;
    private final InvoiceStore store;
    private int invoiceSeq = 1000;

    public CafeteriaSystem() {
        this(new MenuCatalog(),
                new DefaultPricingCalculator(),
                new DefaultTaxPolicy(),
                new DefaultDiscountPolicy(),
                new InvoiceFormatter(),
                new FileStore());
    }

    public CafeteriaSystem(MenuCatalog menu,
                           PricingCalculator pricingCalculator,
                           TaxPolicy taxPolicy,
                           DiscountPolicy discountPolicy,
                           InvoiceFormatter formatter,
                           InvoiceStore store) {
        this.menu = menu;
        this.pricingCalculator = pricingCalculator;
        this.taxPolicy = taxPolicy;
        this.discountPolicy = discountPolicy;
        this.formatter = formatter;
        this.store = store;
    }

    public void addToMenu(MenuItem i) { menu.add(i); }

    public void checkout(String customerType, List<OrderLine> lines) {
        String invId = "INV-" + (++invoiceSeq);

        InvoicePricing pricing = pricingCalculator.calculate(lines, menu);
        double taxPct = taxPolicy.taxPercent(customerType);
        double tax = pricing.subtotal * (taxPct / 100.0);
        double discount = discountPolicy.discountAmount(customerType, pricing.subtotal, pricing.distinctLines);
        double total = pricing.subtotal + tax - discount;

        InvoiceData data = new InvoiceData(invId, pricing.lines, pricing.subtotal, taxPct, tax, discount, total);
        String printable = formatter.format(data);
        System.out.print(printable);

        store.save(invId, printable);
        System.out.println("Saved invoice: " + invId + " (lines=" + store.countLines(invId) + ")");
    }
}
