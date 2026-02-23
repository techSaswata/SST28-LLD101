public class SendResult {
    public final boolean ok;
    public final String errorMessage;

    private SendResult(boolean ok, String errorMessage) {
        this.ok = ok;
        this.errorMessage = errorMessage;
    }

    public static SendResult ok() { return new SendResult(true, null); }

    public static SendResult error(String message) { return new SendResult(false, message); }
}
