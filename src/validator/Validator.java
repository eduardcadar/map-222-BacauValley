package validator;

public interface Validator<E> {
    void validate(E e) throws ValidatorException;
}
