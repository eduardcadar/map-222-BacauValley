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
    public void testToString() {
        Assert.assertEquals(0, us1.toString().compareTo("Pop Ion"));
    }
}
