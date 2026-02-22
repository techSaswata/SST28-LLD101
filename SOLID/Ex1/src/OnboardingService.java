public class OnboardingService {
    private final StudentRepository repo;
    private final StudentInputParser parser;
    private final StudentValidator validator;
    private final StudentIdGenerator idGenerator;

    public OnboardingService(StudentRepository repo, StudentInputParser parser, StudentValidator validator, StudentIdGenerator idGenerator) {
        this.repo = repo;
        this.parser = parser;
        this.validator = validator;
        this.idGenerator = idGenerator;
    }

    public RegistrationResult registerFromRawInput(String raw) {
        StudentInput input = parser.parse(raw);
        java.util.List<String> errors = validator.validate(input);
        if (!errors.isEmpty()) {
            return RegistrationResult.failure(raw, errors);
        }

        String id = idGenerator.nextId(repo.count());
        StudentRecord rec = new StudentRecord(id, input.name, input.email, input.phone, input.program);
        repo.save(rec);
        return RegistrationResult.success(raw, rec, repo.count());
    }
}
