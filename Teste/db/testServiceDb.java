package db;

import Utils.UserFriendDTO;
import domain.*;
import domain.network.Network;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repository.RepoException;
import repository.db.*;
import service.*;
import validator.FriendshipValidator;
import validator.MessageReceiverValidator;
import validator.MessageValidator;
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
    private final FriendshipRequestDbRepo requestsRepo = new FriendshipRequestDbRepo(url, username, password, "requests");

    private final FriendshipService fSrv = new FriendshipService(fRepo,requestsRepo);
    private final MessageDbRepo mRepo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
    private final MessageService mSrv = new MessageService(mRepo);
    private final MessageReceiverDbRepo mrRepo = new MessageReceiverDbRepo(url, username, password, new MessageReceiverValidator(), "receivers");
    private final MessageReceiverService mrSrv = new MessageReceiverService(mrRepo);
    private final Friendship f1 = new Friendship(us2, us1);
    private final Friendship f2 = new Friendship(us3, us1);
    private final Friendship f3 = new Friendship(us2, us4);
    private final Network ntw = new Network(uRepo, fRepo);
    private final Service service = new Service(uSrv, fSrv, mSrv, mrSrv, ntw);

    @Before
    public void setUp() throws Exception {
        service.addUser(us1.getFirstName(), us1.getLastName(), us1.getEmail(), us1.getPassword());
        service.addUser(us2.getFirstName(), us2.getLastName(), us2.getEmail(), us2.getPassword());
        service.addUser(us3.getFirstName(), us3.getLastName(), us3.getEmail(), us3.getPassword());
        service.addUser(us4.getFirstName(), us4.getLastName(), us4.getEmail(), us4.getPassword());
    }

    @After
    public void tearDown() throws Exception {
        mrRepo.clear();
        mRepo.clear();
        fRepo.clear();
        uRepo.clear();
    }

    @Test
    public void testUsersSv() {
        Assert.assertEquals(4, service.usersSize());
        service.removeUser(us1.getEmail());
        Assert.assertEquals(3, service.usersSize());
        List<User> users = service.getUsers();
        Assert.assertTrue(users.contains(us2));
        Assert.assertTrue(users.contains(us3));
        Assert.assertTrue(users.contains(us4));
        Assert.assertFalse(service.usersIsEmpty());
        service.updateUser("andrei", "popescu", "popescu.alex@gmail.com", "parolaa");
        User us = service.getUser("popescu.alex@gmail.com");
        Assert.assertEquals(us.getFirstName(), "andrei");
        Assert.assertEquals(us.getLastName(), "popescu");
    }

    @Test
    public void testGetUserFriends() {
        Assert.assertTrue(service.friendshipsIsEmpty());
        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.addFriendship(f3.getFirst(), f3.getSecond());
        service.acceptFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f3.getFirst(), f3.getSecond());

        List<UserFriendDTO> friendsDTOs = service.getFriendshipsDTO(us1.getEmail());
        Assert.assertEquals(2, friendsDTOs.size());

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
    public void testFriendshipsSv() {
        Assert.assertEquals(0, service.friendshipsSize());

        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f1.getFirst(), f1.getSecond());

        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f2.getFirst(), f2.getSecond());

        Assert.assertEquals(2, service.friendshipsSize());
        Friendship f = service.getFriendship(us1.getEmail(), us2.getEmail());
        Assert.assertNotNull(f);
        f = service.getFriendship(us1.getEmail(), us4.getEmail());
        Assert.assertNull(f);
        List<Friendship> friendships = service.getFriendships();
        Assert.assertTrue(friendships.contains(f1));
        Assert.assertTrue(friendships.contains(f2));
        Assert.assertFalse(friendships.contains(f3));

        service.removeFriendship(f1.getFirst(), f1.getSecond());
        Assert.assertEquals(1, service.friendshipsSize());
    }

    @Test
    public void testFriendRequest() throws Exception {
        service.addFriendship(us1.getEmail(), us2.getEmail());
        Assert.assertEquals(1, service.getUserFriendRequests(us2.getEmail()).size());

        service.rejectFriendship(us1.getEmail(), us2.getEmail());
        try{
            service.addFriendship(us1.getEmail(), us2.getEmail());
            Assert.fail();
        }
        catch (RepoException e){
            Assert.assertEquals("There is already a request send by user", e.getMessage());
        }

        service.addFriendship(us2.getEmail(), us1.getEmail());
        service.acceptFriendship(us2.getEmail(), us1.getEmail());
        Assert.assertEquals(service.getUserFriends(us2.getEmail()).get(0).getEmail(), us1.getEmail());
    }

    @Test
    public void testNetwork() {
        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f1.getFirst(),f1.getSecond() );
        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f2.getFirst(), f2.getSecond());
        service.addFriendship(f3.getFirst(), f3.getSecond());
        service.acceptFriendship(f3.getFirst(), f3.getSecond());
        Assert.assertEquals(1, service.getCommunities().size());
        Assert.assertEquals(1, service.nrCommunities());
        Assert.assertEquals(4, service.getUsersMostFrCom().size());
    }

    @Test
    public void testMessages() {
        service.addFriendship(us1.getEmail(), us2.getEmail());
        service.acceptFriendship(us1.getEmail(), us2.getEmail());


        Message m1 = new Message(us1.getEmail(),"mesaj1");
        Message m2 = new Message(us2.getEmail(),"mesaj2");
        m1 = service.save(m1.getSender(), List.of(us2.getEmail(), us3.getEmail()), m1.getMessage());
        m2 = service.save(m2.getSender(), List.of(us3.getEmail()), m2.getMessage());
        Message r1 = new Message(us2.getEmail(), "reply", m1.getID());
        r1 = service.save(r1.getSender(), List.of(us1.getEmail()), r1.getMessage(), r1.getIdMsgRepliedTo());
        Assert.assertEquals(m1.getID(), service.getMessage(r1.getID()).getIdMsgRepliedTo());
        List<Message> conv = service.getConversation(us1.getEmail(), us2.getEmail());
        Assert.assertEquals(2, conv.size());
        Assert.assertEquals(conv.get(0).getSender(), m1.getSender());
        Assert.assertEquals(conv.get(1).getSender(), r1.getSender());
        conv = service.getConversation(us2.getEmail(), us3.getEmail());
        Assert.assertEquals(0, conv.size());
        service.removeUser(us1.getEmail());
        conv = service.getConversation(us1.getEmail(), us2.getEmail());
        Assert.assertEquals(0, conv.size());
    }
}