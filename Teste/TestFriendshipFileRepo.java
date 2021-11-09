import domain.Friendship;
import domain.User;
import org.junit.Assert;
import org.junit.Test;
import repository.FriendshipRepository;
import repository.RepoException;
import repository.UserRepository;
import repository.file.FriendshipFileRepo;
import repository.memory.UserRepoInMemory;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;

public class TestFriendshipFileRepo {
    private User u1 = new User("adi","popa","adi.popa@yahoo.com");
    private User u2 = new User("alex","popescu","alex.popescu@yahoo.com");
    private User u3 = new User("andrei","radu","andrei.radu@yahoo.com");
    private User u4 = new User("alina","harja","alina.harja@yahoo.com");
    private Validator<User> usrVal = new UserValidator();
    private UserRepository usrRepo = new UserRepoInMemory(usrVal) {{
        save(u1);
        save(u2);
        save(u3);
        save(u4);
    }};
    private String frFile = "testFriendships.csv";
    private Validator<Friendship> frVal = new FriendshipValidator();
    private FriendshipRepository frRepo = new FriendshipFileRepo(frFile, frVal, usrRepo);
    private Friendship f1 = new Friendship(u1, u2);
    private Friendship f2 = new Friendship(u2, u3);
    private Friendship f3 = new Friendship(u2, u4);
    private Friendship f4 = new Friendship(u3, u1);
    private Friendship f5 = new Friendship(u2, u1);

    @Test
    public void testConstructor() {
        // test loadall
        Assert.assertTrue(frRepo.size() == 2);
        Assert.assertTrue(frRepo.getAll().contains(f1));
        Assert.assertTrue(frRepo.getAll().contains(f2));
    }

    @Test
    public void testAddRemove() {
        frRepo.clear();
        frRepo.addFriendship(f1);
        frRepo.addFriendship(f2);
        try {
            frRepo.addFriendship(f1);
            Assert.assertTrue(false);
        } catch (RepoException e) {
            Assert.assertTrue(frRepo.size() == 2);
        }
        frRepo.addFriendship(f3);
        Assert.assertTrue(frRepo.size() == 3);
        frRepo.removeFriendship(f5);
        Assert.assertTrue(frRepo.size() == 2);
        try {
            frRepo.removeFriendship(f1);
            Assert.assertTrue(false);
        } catch (RepoException e) {
            Assert.assertTrue(frRepo.size() == 2);
        }
        frRepo.clear();
        Assert.assertTrue(frRepo.isEmpty());
        frRepo.addFriendship(f1);
        frRepo.addFriendship(f2);
    }
}
