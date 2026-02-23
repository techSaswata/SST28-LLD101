public class CreditsRule implements EligibilityRule {
    private final int minCredits;

    public CreditsRule(int minCredits) { this.minCredits = minCredits; }

    @Override
    public String failureReason(StudentProfile s) {
        if (s.earnedCredits < minCredits) return "credits below " + minCredits;
        return null;
    }
}
