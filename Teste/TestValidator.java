import domain.Friendship;
import domain.User;
import org.junit.Assert;
import org.junit.Test;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;
import validator.ValidatorException;

public class TestValidator {
    private final Validator<User> val1 = new UserValidator();
    private final Validator<Friendship> val2 = new FriendshipValidator();
    private final User u1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private final User u2 = new User("Alex", "Popescu", "popescu.alex@yahoo.com");

    @Test
    public void testValidateNames() {
        // corect
        try {
            val1.validate(new User("ion andrei","pop","ion.pop@yahoo.com"));
        } catch (ValidatorException e) {
            Assert.assertTrue(false);
        }

        // firstName vid
        try {
            val1.validate(new User("", "pop", "i@ya.co"));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }

        // lastName vid
        try {
            val1.validate(new User("ion", "", "i@ya.co"));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }

        // firstName contine si alte caractere inafara de litere si spatii
        try {
            val1.validate(new User("io2n", "pop", "i@ya.co"));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }

        // lastName contine si alte caractere inafara de litere si spatii
        try {
            val1.validate(new User("ion", "p.op", "i@ya.co"));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testValidateEmail() {
        // corect
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo.com"));
        } catch (ValidatorException e) {
            Assert.fail();
        }

        // corect
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo.co.uk"));
        } catch (ValidatorException e) {
            Assert.fail();
        }

        // email nu are '.' dupa '@'
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo"));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }

        // email are 3 '.' dupa '@'
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo.co.uk.ro"));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }

        // email e vid
        try {
            val1.validate(new User("ionica","popescu",""));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testValidateFriendship() {
        val2.validate(new Friendship(u1, u2));
        val2.validate(new Friendship(u2, u1));
        try {
            val2.validate(new Friendship(u1, u1));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }
        try {
            val2.validate(new Friendship(u2, u2));
            Assert.fail();
        } catch (ValidatorException e) {
            Assert.assertTrue(true);
        }
    }
}
