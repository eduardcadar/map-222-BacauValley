package db;

import domain.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repository.RepoException;
import repository.db.UserDbRepo;
import validator.UserValidator;

import java.util.List;

public class testUserRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo repo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");

    @Before
    public void setUp() throws Exception {
        repo.save(us1);
        repo.save(us2);
        repo.save(us3);
        repo.save(us4);
    }

    @After
    public void tearDown() throws Exception {
        repo.clear();
    }

    @Test
    public void testConstructorDb() {
        Assert.assertEquals(4, repo.size());
        List<User> users = repo.getAll();
        Assert.assertTrue(users.contains(us1));
        Assert.assertTrue(users.contains(us2));
        Assert.assertTrue(users.contains(us3));
        Assert.assertTrue(users.contains(us4));
    }

    @Test
    public void testAddRemoveUserDb() {
        repo.save(new User("cosmin","harja","cosmin.h@yahoo.com"));
        Assert.assertEquals(5, repo.size());
        Assert.assertNotNull(repo.getUser("cosmin.h@yahoo.com"));
        try {
            repo.save(new User("cosmin","harja","cosmin.h@yahoo.com"));
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
        Assert.assertNull(repo.getUser("email.em@y.c"));
        repo.remove("cosmin.h@yahoo.com");
        Assert.assertEquals(4, repo.size());
    }

    @Test
    public void testUpdateUserDb() {
        repo.update(new User("toma","furdui","adi.popa@yahoo.com"));
        User u = repo.getUser("adi.popa@yahoo.com");
        Assert.assertEquals("toma", u.getFirstName());
        Assert.assertEquals("furdui", u.getLastName());
        repo.update(us1);
    }

    @Test
    public void testClearUsersDb() {
        repo.clear();
        Assert.assertTrue(repo.isEmpty());
        repo.save(us1);
        repo.save(us2);
        repo.save(us3);
        repo.save(us4);
    }
}
