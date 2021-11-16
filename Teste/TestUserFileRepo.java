import domain.User;
import org.junit.Assert;
import org.junit.Test;
import repository.RepoException;
import repository.file.UserFileRepo;
import validator.UserValidator;
import validator.Validator;

public class TestUserFileRepo {
    private final String filename = "testUsers.csv";
    private final Validator<User> val = new UserValidator();
    private final UserFileRepo repo = new UserFileRepo(filename, val);
    private final User u1 = new User("adi","popa","adi.popa@yahoo.com");
    private final User u2 = new User("alex","popescu","alex.popescu@yahoo.com");
    private final User u3 = new User("andrei","radu","andrei.radu@yahoo.com");
    private final User u4 = new User("alina","harja","adi.popa@yahoo.com");

    @Test
    public void testConstructor() {
        // test loadall
        Assert.assertEquals(2, repo.size());
        Assert.assertTrue(repo.getAll().contains(u1));
        Assert.assertTrue(repo.getAll().contains(u3));
    }

    @Test
    public void testAddRemove() {
        try {
            repo.save(u4);
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }

        try {
            repo.save(u2);
            Assert.assertEquals(3, repo.size());
            repo.remove(u2.getEmail());
            Assert.assertEquals(2, repo.size());
        } catch (RepoException e) {
            Assert.fail();
        }

        try {
            repo.remove(u2.getEmail());
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(2, repo.size());
    }

    @Test
    public void testUpdate() {
        User u5 = new User("Ion","Pop","adi.popa@yahoo.com");
        User u6 = new User("Ion","Popa","popa.ion@yahoo.com");
        repo.update(u5.getFirstName(), u5.getLastName(), u5.getEmail());
        Assert.assertEquals(0, repo.getUser(u5.getEmail()).getLastName().compareTo("Pop"));
        repo.update(u1.getFirstName(), u1.getLastName(), u1.getEmail());
        Assert.assertEquals(0, repo.getUser(u5.getEmail()).getLastName().compareTo("popa"));
        try {
            repo.update(u6.getFirstName(), u6.getLastName(), u6.getEmail());
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testClear() {
        repo.clear();
        Assert.assertTrue(repo.isEmpty());
        repo.save(u1);
        repo.save(u3);
        Assert.assertEquals(2, repo.size());
    }
}
