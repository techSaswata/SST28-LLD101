import java.util.List;

public class EligibilityEngine {
    private final FakeEligibilityStore store;
    private final java.util.List<EligibilityRule> rules;

    public EligibilityEngine(FakeEligibilityStore store) {
        this(store, defaultRules());
    }

    public EligibilityEngine(FakeEligibilityStore store, java.util.List<EligibilityRule> rules) {
        this.store = store;
        this.rules = rules;
    }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityEngineResult r = evaluate(s); // giant conditional inside
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityEngineResult evaluate(StudentProfile s) {
        java.util.List<String> reasons = new java.util.ArrayList<>();
        String status = "ELIGIBLE";

        for (EligibilityRule rule : rules) {
            String reason = rule.failureReason(s);
            if (reason != null) {
                status = "NOT_ELIGIBLE";
                reasons.add(reason);
                break;
            }
        }

        return new EligibilityEngineResult(status, reasons);
    }

    private static java.util.List<EligibilityRule> defaultRules() {
        return java.util.List.of(
                new DisciplinaryFlagRule(),
                new CgrRule(8.0),
                new AttendanceRule(75),
                new CreditsRule(20)
        );
    }
}

class EligibilityEngineResult {
    public final String status;
    public final List<String> reasons;
    public EligibilityEngineResult(String status, List<String> reasons) {
        this.status = status;
        this.reasons = reasons;
    }
}
