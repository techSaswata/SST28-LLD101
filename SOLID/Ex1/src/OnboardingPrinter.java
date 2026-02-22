public class OnboardingPrinter {
    public void print(RegistrationResult result) {
        System.out.println("INPUT: " + result.rawInput);
        if (!result.isSuccess()) {
            System.out.println("ERROR: cannot register");
            for (String e : result.errors) System.out.println("- " + e);
            return;
        }
        System.out.println("OK: created student " + result.record.id);
        System.out.println("Saved. Total students: " + result.totalCount);
        System.out.println("CONFIRMATION:");
        System.out.println(result.record);
    }
}
