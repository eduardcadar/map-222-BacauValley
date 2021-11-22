import domain.Friendship;
import domain.FriendshipRequest;
import domain.REQUESTSTATE;
import domain.User;
import org.junit.Assert;
import org.junit.Test;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;
import validator.ValidatorException;

public class TestFriendshipRequest {
    private final FriendshipRequest friendshipRequest = new FriendshipRequest("1", "2");


    @Test
    public void testConstructor() {
        Assert.assertEquals(friendshipRequest.getFirst(), "1");
        Assert.assertEquals(friendshipRequest.getSecond(), "2");
        Assert.assertEquals(friendshipRequest.getState(), REQUESTSTATE.PENDING);
    }

    @Test
    public void testChangeState() {
        friendshipRequest.setState(REQUESTSTATE.APPROVED);
        Assert.assertEquals(friendshipRequest.getState(), REQUESTSTATE.APPROVED);

        friendshipRequest.setState(REQUESTSTATE.REJECTED);
        Assert.assertEquals(friendshipRequest.getState(), REQUESTSTATE.REJECTED);

    }
}
