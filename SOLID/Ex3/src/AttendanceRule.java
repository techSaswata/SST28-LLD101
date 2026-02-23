public class AttendanceRule implements EligibilityRule {
    private final int minAttendance;

    public AttendanceRule(int minAttendance) { this.minAttendance = minAttendance; }

    @Override
    public String failureReason(StudentProfile s) {
        if (s.attendancePct < minAttendance) return "attendance below " + minAttendance;
        return null;
    }
}
