import java.util.List;

public class InvoiceData {
    public final String invoiceId;
    public final List<InvoiceLine> lines;
    public final double subtotal;
    public final double taxPercent;
    public final double taxAmount;
    public final double discount;
    public final double total;

    public InvoiceData(String invoiceId,
                       List<InvoiceLine> lines,
                       double subtotal,
                       double taxPercent,
                       double taxAmount,
                       double discount,
                       double total) {
        this.invoiceId = invoiceId;
        this.lines = lines;
        this.subtotal = subtotal;
        this.taxPercent = taxPercent;
        this.taxAmount = taxAmount;
        this.discount = discount;
        this.total = total;
    }
}
