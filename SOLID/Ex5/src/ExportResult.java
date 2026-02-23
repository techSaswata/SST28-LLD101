public class ExportResult {
    public final boolean ok;
    public final String contentType;
    public final byte[] bytes;
    public final String errorMessage;

    private ExportResult(boolean ok, String contentType, byte[] bytes, String errorMessage) {
        this.ok = ok;
        this.contentType = contentType;
        this.bytes = bytes;
        this.errorMessage = errorMessage;
    }

    public static ExportResult ok(String contentType, byte[] bytes) {
        return new ExportResult(true, contentType, bytes, null);
    }

    public static ExportResult error(String message) {
        return new ExportResult(false, "text/plain", new byte[0], message);
    }
}
