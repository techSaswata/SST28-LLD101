import java.nio.charset.StandardCharsets;

public class PdfExporter extends Exporter {
    @Override
    public ExportResult export(ExportRequest req) {
        ExportRequest safe = normalize(req);
        if (safe.body.length() > 20) {
            return ExportResult.error("PDF cannot handle content > 20 chars");
        }
        String fakePdf = "PDF(" + safe.title + "):" + safe.body;
        return ExportResult.ok("application/pdf", fakePdf.getBytes(StandardCharsets.UTF_8));
    }
}
