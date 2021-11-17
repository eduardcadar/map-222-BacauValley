package db;

import domain.Message;
import domain.ReplyMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repository.db.MessageDbRepo;
import validator.MessageValidator;

public class testMessageRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final MessageDbRepo repo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
    private final Message m1 = new Message("eu","mesaj1");
    private final Message m2 = new Message("tu","mesaj2");
    private final ReplyMessage r1 = new ReplyMessage("eu", "reply", 2);

    @Before
    public void setUp() throws Exception {
        repo.clear();
    }

    @Test
    public void testAddMessage() {
        repo.save(m1);
        Assert.assertEquals(1, repo.size());
        repo.save(m2);
        Assert.assertEquals(2, repo.size());
        repo.save(r1);
        Assert.assertEquals(3, repo.size());
    }
}
