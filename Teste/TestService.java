import domain.Friendship;
import domain.User;
import domain.network.Network;
import org.junit.Assert;
import org.junit.Test;
import repository.FriendshipRepository;
import repository.file.FriendshipFileRepo;
import repository.file.UserFileRepo;
import service.FriendshipService;
import service.Service;
import service.UserService;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;

public class TestService {
    private final String usFile = "testUsersSv.csv";
    private final Validator<User> uVal = new UserValidator();
    private final UserFileRepo uRepo = new UserFileRepo(usFile, uVal);
    private final UserService uSrv = new UserService(uRepo);
    private final String frFile = "testFriendshipsSv.csv";
    private final Validator<Friendship> fVal = new FriendshipValidator();
    private final FriendshipRepository fRepo = new FriendshipFileRepo(frFile, fVal, uRepo);
    private final FriendshipService fSrv = new FriendshipService(fRepo);
    private final Network nw = new Network(uRepo, fRepo);
    private final Service sv = new Service(uSrv, fSrv, nw);
    private final User us1 = new User("adi","popa","adi.popa@yahoo.com");
    private final User us2 = new User("alex","popescu","alex.popescu@yahoo.com");

    @Test
    public void testUsersSv() {
        int nr = sv.usersSize();
        sv.addUser(new User("firstname","lastname","email@email.email"));
        Assert.assertEquals(sv.usersSize(), nr + 1);
        Assert.assertEquals(0, sv.getUser("email@email.email").getEmail().compareTo("email@email.email"));
        sv.removeUser("email@email.email");
        Assert.assertEquals(sv.usersSize(), nr);
        Assert.assertEquals(sv.getUsers().size(), sv.usersSize());
        Assert.assertFalse(sv.usersIsEmpty());
        User u = new User("alexandru","cadar","adi.popa@yahoo.com");
        sv.updateUser(u);
        Assert.assertEquals("alexandru", sv.getUser(us1.getEmail()).getFirstName());
        Assert.assertEquals("cadar", sv.getUser(us1.getEmail()).getLastName());
        sv.updateUser(us1);
    }

    @Test
    public void testFriendshipsSv() {
        int nr = sv.friendshipsSize();
        sv.addFriendship(new Friendship(us1, us2));
        Assert.assertNotNull(sv.getFriendship(us1.getEmail(), us2.getEmail()));
        Assert.assertEquals(sv.friendshipsSize(), nr + 1);
        sv.removeFriendship(new Friendship(us2, us1));
        Assert.assertEquals(sv.friendshipsSize(), nr);
        Assert.assertEquals(sv.getFriendships().size(), sv.friendshipsSize());
        Assert.assertFalse(sv.friendshipsIsEmpty());
    }

    @Test
    public void testNetworkSv() {
        Assert.assertEquals(3, sv.getCommunities().size());
        Assert.assertEquals(3, sv.nrCommunities());
        Assert.assertEquals(10, sv.getUsersMostFrCom().size());
    }
}
