import java.util.ArrayList;
import java.util.List;

public class StudentValidator {
    public List<String> validate(StudentInput input) {
        List<String> errors = new ArrayList<>();
        if (input.name.isBlank()) errors.add("name is required");
        if (input.email.isBlank() || !input.email.contains("@")) errors.add("email is invalid");
        if (input.phone.isBlank() || !input.phone.chars().allMatch(Character::isDigit)) errors.add("phone is invalid");
        if (!(input.program.equals("CSE") || input.program.equals("AI") || input.program.equals("SWE"))) {
            errors.add("program is invalid");
        }
        return errors;
    }
}
