public class EvaluationPipeline {
    private final Rubric rubric;
    private final PlagiarismCheckerService plagiarismChecker;
    private final CodeGraderService grader;
    private final ReportWriterService writer;

    public EvaluationPipeline(Rubric rubric,
                              PlagiarismCheckerService plagiarismChecker,
                              CodeGraderService grader,
                              ReportWriterService writer) {
        this.rubric = rubric;
        this.plagiarismChecker = plagiarismChecker;
        this.grader = grader;
        this.writer = writer;
    }

    public void evaluate(Submission sub) {
        int plag = plagiarismChecker.check(sub);
        System.out.println("PlagiarismScore=" + plag);

        int code = grader.grade(sub, rubric);
        System.out.println("CodeScore=" + code);

        String reportName = writer.write(sub, plag, code);
        System.out.println("Report written: " + reportName);

        int total = plag + code;
        String result = (total >= 90) ? "PASS" : "FAIL";
        System.out.println("FINAL: " + result + " (total=" + total + ")");
    }
}
