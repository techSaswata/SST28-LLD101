import java.nio.charset.StandardCharsets;

public class CsvExporter extends Exporter {
    @Override
    public ExportResult export(ExportRequest req) {
        ExportRequest safe = normalize(req);
        String body = safe.body.replace("\n", " ").replace(",", " ");
        String csv = "title,body\n" + safe.title + "," + body + "\n";
        return ExportResult.ok("text/csv", csv.getBytes(StandardCharsets.UTF_8));
    }
}
