public class DisciplinaryFlagRule implements EligibilityRule {
    @Override
    public String failureReason(StudentProfile s) {
        if (s.disciplinaryFlag != LegacyFlags.NONE) return "disciplinary flag present";
        return null;
    }
}
