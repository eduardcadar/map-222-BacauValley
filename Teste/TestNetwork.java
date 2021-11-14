import domain.Friendship;
import domain.network.MostFriendlyCommunity;
import domain.network.Network;
import domain.User;
import org.junit.Assert;
import org.junit.Test;
import repository.FriendshipRepository;
import repository.file.FriendshipFileRepo;
import repository.file.UserFileRepo;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;

import java.util.List;
import java.util.Map;

public class TestNetwork {
    private final String usFile = "testUsersNw.csv";
    private final Validator<User> uVal = new UserValidator();
    private final UserFileRepo uRepo = new UserFileRepo(usFile, uVal);
    private final String frFile = "testFriendshipsNw.csv";
    private final Validator<Friendship> fVal = new FriendshipValidator();
    private final FriendshipRepository fRepo = new FriendshipFileRepo(frFile, fVal, uRepo);

    @Test
    public void testConstructor() {
        // tests countCommunities
        Network nw = new Network(uRepo, fRepo);
        Assert.assertEquals(3, nw.getNrCommunities());
    }

    @Test
    public void testCommunities() {
        Network nw = new Network(uRepo, fRepo);
        Map<Integer, List<String>> comms = nw.getCommunities();
        Assert.assertEquals(3, comms.size());
    }

    @Test
    public void testMostFrCommunity() {
        // tests MostFriendlyCommunity
        Network nw = new Network(uRepo, fRepo);
        MostFriendlyCommunity mfc = nw.getmfrCom();
        Assert.assertEquals(10, mfc.getNrUsers());
        List<User> usrs = nw.getUsersMostFrCom();
        Assert.assertEquals(10, usrs.size());
    }
}
