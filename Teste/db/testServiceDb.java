package db;

import domain.Friendship;
import domain.User;
import domain.network.Network;
import org.junit.After;
import org.junit.Assert;
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
    private final FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "testfriendships");
    private final FriendshipService fSrv = new FriendshipService(fRepo);
    private final Friendship f1 = new Friendship(us1, us2);
    private final Friendship f2 = new Friendship(us1, us3);
    private final Friendship f3 = new Friendship(us2, us4);
    private final Network ntw = new Network(uRepo, fRepo);
    private final Service service = new Service(uSrv, fSrv, ntw);

    @After
    public void tearDown() throws Exception {
        fRepo.clear();
        uRepo.clear();
    }

    @Test
    public void testGetUserFriends() {
        service.addUser(us1);
        service.addUser(us2);
        service.addUser(us3);
        service.addUser(us4);
        service.addFriendship(f1);
        service.addFriendship(f2);
        service.addFriendship(f3);
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
}
