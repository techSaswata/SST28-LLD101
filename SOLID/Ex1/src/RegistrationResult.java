import java.util.List;

public class RegistrationResult {
    public final String rawInput;
    public final List<String> errors;
    public final StudentRecord record;
    public final int totalCount;

    private RegistrationResult(String rawInput, List<String> errors, StudentRecord record, int totalCount) {
        this.rawInput = rawInput;
        this.errors = errors;
        this.record = record;
        this.totalCount = totalCount;
    }

    public static RegistrationResult success(String rawInput, StudentRecord record, int totalCount) {
        return new RegistrationResult(rawInput, List.of(), record, totalCount);
    }

    public static RegistrationResult failure(String rawInput, List<String> errors) {
        return new RegistrationResult(rawInput, errors, null, 0);
    }

    public boolean isSuccess() { return errors.isEmpty(); }
}
