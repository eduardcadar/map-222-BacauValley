package validator;

import domain.User;

import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    private Pattern namePattern = Pattern.compile("^[a-zA-Z\s]+$");
    private Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){1,2}$");

    /**
     * Valideaza un utilizator
     * @param user - utilizatorul care va fi  validat
     * @throws ValidatorException - daca nume/prenume sunt campuri goale sau email nu este valid
     */
    public void validate(User user) throws ValidatorException {
        if (!namePattern.matcher(user.getLastName()).matches()) throw new ValidatorException("Numele trebuie sa contina doar litere");
        if (!namePattern.matcher(user.getFirstName()).matches()) throw new ValidatorException("Prenumele trebuie sa contina doar litere");
        if (!emailPattern.matcher(user.getEmail()).matches()) throw new ValidatorException("Nu ati introdus un email valid");
    }
}
