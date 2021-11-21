package db;

import domain.Friendship;
import domain.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repository.RepoException;
import repository.db.FriendshipDbRepo;
import repository.db.UserDbRepo;
import validator.FriendshipValidator;
import validator.UserValidator;

import java.time.LocalDate;
import java.util.List;

public class testFriendshipRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");
    private final FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships");
    private final Friendship f1 = new Friendship(us1, us2, LocalDate.now());
    private final Friendship f2 = new Friendship(us1, us3, LocalDate.now());
    private final Friendship f3 = new Friendship(us2, us4, LocalDate.now());

    @Before
    public void setUp() throws Exception {
        uRepo.save(us1);
        uRepo.save(us2);
        uRepo.save(us3);
        uRepo.save(us4);
        fRepo.addFriendship(f1);
        fRepo.addFriendship(f2);
        fRepo.addFriendship(f3);

    }

    @After
    public void tearDown() throws Exception {
        fRepo.clear();
        uRepo.clear();
    }

    @Test
    public void TestConstructorDb() {
        Assert.assertEquals(3, fRepo.size());
        List<Friendship> fships = fRepo.getAll();
        Assert.assertTrue(fships.contains(f1));
        Assert.assertTrue(fships.contains(f2));
        Assert.assertTrue(fships.contains(f3));
    }

    @Test
    public void testAddRemoveFshipDb() {
        fRepo.addFriendship(new Friendship(us1, us4));
        Assert.assertEquals(4, fRepo.size());
        Assert.assertNotNull(fRepo.getFriendship(us1.getEmail(), us4.getEmail()));
        try {
            fRepo.addFriendship(new Friendship(us1, us4));
            Assert.fail();
        } catch (RepoException e) {
            Assert.assertTrue(true);
        }
        fRepo.removeFriendship(new Friendship(us1, us4));
        Assert.assertNull(fRepo.getFriendship(us1.getEmail(), us4.getEmail()));
        Assert.assertEquals(3, fRepo.size());
    }

    @Test
    public void testClearFriendshipsDb() {
        fRepo.clear();
        Assert.assertTrue(fRepo.isEmpty());
    }

    @Test
    public void testGetUserFriendsDb() {
        List<String> friends = fRepo.getUserFriends(us1.getEmail());
        Assert.assertEquals(2,friends.size());
        Assert.assertTrue(friends.contains(us2.getEmail()));
        Assert.assertTrue(friends.contains(us3.getEmail()));
    }

    @Test
    public void testRemoveUserFriendshipsDb() {
        fRepo.removeUserFships(us1.getEmail());
        Assert.assertEquals(1, fRepo.size());
        fRepo.removeUserFships(us1.getEmail());
        Assert.assertEquals(1, fRepo.size());
        fRepo.addFriendship(f1);
        fRepo.addFriendship(f2);
    }
}
