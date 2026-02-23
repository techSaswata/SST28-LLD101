import java.nio.charset.StandardCharsets;

public class JsonExporter extends Exporter {
    @Override
    public ExportResult export(ExportRequest req) {
        ExportRequest safe = normalize(req);
        String json = "{\"title\":\"" + escape(safe.title) + "\",\"body\":\"" + escape(safe.body) + "\"}";
        return ExportResult.ok("application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }
}
