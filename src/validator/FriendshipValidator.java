package validator;

import domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Valideaza o relatie de prietenie
     * @param friendship - relatia de prietenie care va fi validata
     * @throws ValidatorException - daca e contine acelasi utilizator de 2 ori
     */
    @Override
    public void validate(Friendship friendship) throws ValidatorException {
        if (friendship.getFirst().equals(friendship.getSecond())) throw new ValidatorException("Introduceti 2 utilizatori diferiti ca prieteni");
    }
}
