package db;

import domain.FRIENDSHIPSTATE;
import domain.Friendship;
import domain.User;
import domain.network.Network;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repository.db.FriendshipDbRepo;
import repository.db.UserDbRepo;
import service.FriendshipService;
import service.Service;
import service.UserService;
import validator.FriendshipValidator;
import validator.UserValidator;

import java.util.List;

public class testServiceDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final UserService uSrv = new UserService(uRepo);
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");
    private final FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships");
    private final FriendshipService fSrv = new FriendshipService(fRepo);
    private final Friendship f1 = new Friendship(us2, us1);
    private final Friendship f2 = new Friendship(us3, us1);
    private final Friendship f3 = new Friendship(us2, us4);
    private final Network ntw = new Network(uRepo, fRepo);
    private final Service service = new Service(uSrv, fSrv, ntw);

    @Before
    public void setUp() throws Exception {
        service.addUser(us1.getFirstName(), us1.getLastName(), us1.getEmail(), us1.getPassword());
        service.addUser(us2.getFirstName(), us2.getLastName(), us2.getEmail(), us2.getPassword());
        service.addUser(us3.getFirstName(), us3.getLastName(), us3.getEmail(), us3.getPassword());
        service.addUser(us4.getFirstName(), us4.getLastName(), us4.getEmail(), us4.getPassword());
    }

    @After
    public void tearDown() throws Exception {
        fRepo.clear();
        uRepo.clear();
    }

    @Test
    public void testGetUserFriends() {
        Assert.assertTrue(service.friendshipsIsEmpty());
        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.addFriendship(f3.getFirst(), f3.getSecond());
        service.acceptFriendship(f1);
        service.acceptFriendship(f2);
        service.acceptFriendship(f3);
        List<User> friends = service.getUserFriends(us1.getEmail());
        Assert.assertEquals(2, friends.size());
        Assert.assertTrue(friends.contains(us2));
        Assert.assertTrue(friends.contains(us3));
        friends = service.getUserFriends(us2.getEmail());
        Assert.assertEquals(2, friends.size());
        Assert.assertTrue(friends.contains(us1));
        Assert.assertTrue(friends.contains(us4));
        friends = service.getUserFriends(us3.getEmail());
        Assert.assertEquals(1, friends.size());
        Assert.assertTrue(friends.contains(us1));
        friends = service.getUserFriends(us4.getEmail());
        Assert.assertEquals(1, friends.size());
        Assert.assertTrue(friends.contains(us2));
        List<User> notFriends = service.getNotFriends(us1.getEmail());
        Assert.assertEquals(1, notFriends.size());
        Assert.assertTrue(notFriends.contains(us4));
        notFriends = service.getNotFriends(us3.getEmail());
        Assert.assertEquals(2, notFriends.size());
        Assert.assertTrue(notFriends.contains(us2));
        Assert.assertTrue(notFriends.contains(us4));
    }

    @Test
    public void testFriendRequest() throws Exception {
        service.addFriendship(us1.getEmail(),us2.getEmail());
        Friendship f = service.getFriendship(us1.getEmail(), us2.getEmail());
        Assert.assertEquals(f.getState(), FRIENDSHIPSTATE.PENDING);
        Assert.assertEquals(1, service.getUserFriendRequests(us2.getEmail()).size());
        service.acceptFriendship(f);
        Assert.assertEquals(f.getState(), FRIENDSHIPSTATE.APPROVED);
        Assert.assertNotNull(f.getDate());
    }

    @Test
    public void testNetwork() {
        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f1);
        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f2);
        service.addFriendship(f3.getFirst(), f3.getSecond());
        service.acceptFriendship(f3);
        Assert.assertEquals(1, service.getCommunities().size());
        Assert.assertEquals(1, service.nrCommunities());
        Assert.assertEquals(4, service.getUsersMostFrCom().size());
    }
}
