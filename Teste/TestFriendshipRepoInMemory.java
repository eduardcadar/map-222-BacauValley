import domain.Friendship;
import domain.User;
import org.junit.Assert;
import org.junit.Test;
import repository.RepoException;
import repository.UserRepository;
import repository.memory.FriendshipRepoInMemory;
import repository.memory.UserRepoInMemory;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;

import java.util.List;

public class TestFriendshipRepoInMemory {
    private Validator<User> userVal = new UserValidator();
    private UserRepository userRepo = new UserRepoInMemory(userVal);
    private Validator<Friendship> val = new FriendshipValidator();
    private FriendshipRepoInMemory repo = new FriendshipRepoInMemory(val, userRepo);
    private User u1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private User u2 = new User("Alex", "Popescu", "popescu.alex@yahoo.com");
    private User u3 = new User("Andrei", "Pop", "pop.andrei@yahoo.com");
    private User u4 = new User("Ionica", "Popa", "pop.ionn@yahoo.com");
    private Friendship f1 = new Friendship(u1, u2);
    private Friendship f2 = new Friendship(u1, u3);
    private Friendship f3 = new Friendship(u2, u4);
    private Friendship f4 = new Friendship(u3, u1);

    @Test
    public void testAdd() {
        Assert.assertTrue(repo.isEmpty());
        repo.addFriendship(f1);
        Assert.assertTrue(repo.size() == 1);
        repo.addFriendship(f2);
        Assert.assertTrue(repo.size() == 2);
        try {
            repo.addFriendship(f4);
            Assert.assertTrue(false);
        } catch (RepoException e) {
            Assert.assertTrue(repo.size() == 2);
        }
        repo.addFriendship(f3);
        Assert.assertTrue(repo.size() == 3);
        repo.clear();
        Assert.assertTrue(repo.isEmpty());
    }

    @Test
    public void testRemove() {
        repo.addFriendship(f1);
        try {
            repo.removeFriendship(f2);
            Assert.assertTrue(false);
        } catch (RepoException e) {
            Assert.assertTrue(repo.size() == 1);
        }
        repo.addFriendship(f2);
        Assert.assertTrue(repo.size() == 2);
        repo.removeFriendship(f4);
        Assert.assertTrue(repo.size() == 1);
        repo.clear();
        Assert.assertTrue(repo.isEmpty());
    }

    @Test
    public void testGetAll() {
        repo.addFriendship(f1);
        repo.addFriendship(f2);
        repo.addFriendship(f3);
        List<Friendship> frs = repo.getAll();
        Assert.assertTrue(frs.size() == 3);
        Assert.assertTrue(frs.contains(f1));
        Assert.assertTrue(frs.contains(f2));
        Assert.assertTrue(frs.contains(f3));
        Assert.assertTrue(frs.contains(f4));
    }

    @Test
    public void testGetFriendship() {
        repo.addFriendship(f3);
        Assert.assertNull(repo.getFriendship(u1.getEmail(), u4.getEmail()));
        Assert.assertNotNull(repo.getFriendship(u2.getEmail(), u4.getEmail()));
        repo.clear();
    }

    @Test
    public void testGetFriends() {
        repo.addFriendship(f1);
        repo.addFriendship(f2);
        repo.addFriendship(f3);
        List<String> friends = repo.getUserFriends(u1.getEmail());
        Assert.assertEquals(2, friends.size());
        Assert.assertTrue(friends.contains(u2.getEmail()));
        Assert.assertTrue(friends.contains(u3.getEmail()));
        repo.clear();
    }
}
