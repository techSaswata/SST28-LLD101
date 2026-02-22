import java.util.List;

public class InvoicePricing {
    public final List<InvoiceLine> lines;
    public final double subtotal;
    public final int distinctLines;

    public InvoicePricing(List<InvoiceLine> lines, double subtotal, int distinctLines) {
        this.lines = lines;
        this.subtotal = subtotal;
        this.distinctLines = distinctLines;
    }
}
