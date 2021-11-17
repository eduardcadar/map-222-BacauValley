import domain.User;
import org.junit.Assert;
import org.junit.Test;

public class TestUser {
    private final User us1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private final User us2 = new User("Adi", "Radu", "radu.adi@gmail.com");

    @Test
    public void testGetters() {
        Assert.assertEquals(0, us1.getFirstName().compareTo("Ion"));
        Assert.assertEquals(0, us1.getLastName().compareTo("Pop"));
        Assert.assertEquals(0, us1.getEmail().compareTo("pop.ion@yahoo.com"));
    }

    @Test
    public void testUpdate() {
        us1.update("gigel","g","parola");
        Assert.assertEquals(us1.getLastName(),"g");
        Assert.assertEquals(us1.getFirstName(),"gigel");
        us1.update("Ion","Pop","000000");
    }

    @Test
    public void testToString() {
        Assert.assertEquals(0, us1.toString().compareTo("Pop Ion"));
    }
}
