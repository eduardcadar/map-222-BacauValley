import domain.User;
import repository.RepoException;
import org.junit.Assert;
import org.junit.Test;
import repository.memory.UserRepoInMemory;
import validator.UserValidator;
import validator.Validator;

public class TestUserRepoInMemory {
    private final Validator<User> val = new UserValidator();
    private final UserRepoInMemory repo = new UserRepoInMemory(val);
    private final User u1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private final User u2 = new User("Alex", "Popescu", "popescu.alex@yahoo.com");
    private final User u3 = new User("Andrei", "Pop", "pop.andrei@yahoo.com");
    private final User u4 = new User("Ionica", "Popa", "pop.ion@yahoo.com");

    @Test
    public void testAdd() {
        try {
            repo.save(u1);
            Assert.assertEquals(1, repo.size());
        } catch (RepoException e) {
            Assert.fail();
        }

        try {
            repo.save(u2);
            repo.save(u3);
            Assert.assertEquals(3, repo.size());
        } catch (RepoException e) {
            Assert.fail();
        }

        try {
            repo.save(u4);
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
        repo.clear();
        Assert.assertEquals(0, repo.size());
    }

    @Test
    public void testRemove() {
        try {
            repo.save(u1);
            repo.getUser(u1.getEmail());
        } catch (RepoException e) {
            Assert.fail();
        }
        try {
            repo.remove(u1.getEmail());
            repo.getUser(u1.getEmail());
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertFalse(false);
        }
        try {
            repo.remove(u1.getEmail());
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetUser() {
        repo.save(u1);
        repo.save(u2);
        Assert.assertEquals(repo.getUser(u1.getEmail()), u1);
        try {
            repo.getUser(u3.getEmail());
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
        repo.clear();
    }

    @Test
    public void testUpdate() {
        User u5 = new User("Ion","Popa","pop.ion@yahoo.com");
        repo.save(u5);
        User u6 = new User("Ion","Popa","popa.ion@yahoo.com");
        repo.update(u5.getFirstName(), u5.getLastName(), u5.getEmail());
        Assert.assertEquals(0, repo.getUser(u5.getEmail()).getLastName().compareTo("Popa"));
        repo.update(u1.getFirstName(), u1.getLastName(), u1.getEmail());
        Assert.assertEquals(0, repo.getUser(u5.getEmail()).getLastName().compareTo("Pop"));
        try {
            repo.update(u6.getFirstName(), u6.getLastName(), u6.getEmail());
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetAll() {
        Assert.assertEquals(0, repo.getAll().size());
        repo.save(u1);
        repo.save(u2);
        repo.save(u3);
        Assert.assertEquals(3, repo.getAll().size());
        Assert.assertTrue(repo.getAll().contains(u1));
        Assert.assertTrue(repo.getAll().contains(u2));
        Assert.assertTrue(repo.getAll().contains(u3));
        repo.clear();
    }
}


