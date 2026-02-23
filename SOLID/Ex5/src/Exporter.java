public abstract class Exporter {
    // implied "contract" but not enforced (smell)
    public abstract ExportResult export(ExportRequest req);

    protected ExportRequest normalize(ExportRequest req) {
        if (req == null) return new ExportRequest("", "");
        String title = req.title == null ? "" : req.title;
        String body = req.body == null ? "" : req.body;
        if (title == req.title && body == req.body) return req;
        return new ExportRequest(title, body);
    }
}
