public class Main {
    public static void main(String[] args) {
        System.out.println("=== Student Onboarding ===");
        FakeDb db = new FakeDb();
        OnboardingService svc = new OnboardingService(
                db,
                new StudentInputParser(),
                new StudentValidator(),
                new DefaultStudentIdGenerator()
        );

        String raw = "name=Riya;email=riya@sst.edu;phone=9876543210;program=CSE";
        RegistrationResult result = svc.registerFromRawInput(raw);
        new OnboardingPrinter().print(result);

        System.out.println();
        System.out.println("-- DB DUMP --");
        System.out.print(TextTable.render3(db));
    }
}
