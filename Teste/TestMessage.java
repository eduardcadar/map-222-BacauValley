import domain.Message;
import domain.ReplyMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestMessage {
    private final Message m1 = new Message("eu","mesaj1");
    private final Message m2 = new Message("tu","mesaj2", Arrays.asList("eu", "el"));
    private final ReplyMessage r1 = new ReplyMessage("eu", "reply", 2);

    @Test
    public void testGetters() {
        Assert.assertEquals("eu", m1.getSender());
        Assert.assertEquals("tu", m2.getSender());
        Assert.assertEquals("mesaj1", m1.getMessage());
        Assert.assertEquals("mesaj2", m2.getMessage());
    }
}
