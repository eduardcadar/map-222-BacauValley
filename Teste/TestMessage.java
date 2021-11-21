import domain.Message;
import domain.MessageReceiver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestMessage {
    private final Message m1 = new Message("eu","mesaj1");
    private final Message m2 = new Message("tu","mesaj2") {{
        setReceivers(Arrays.asList("eu", "el"));
    }};
    private final MessageReceiver mr1 = new MessageReceiver(1, "tu");
    @Test
    public void testGetters() {
        Assert.assertEquals("eu", m1.getSender());
        Assert.assertEquals("tu", m2.getSender());
        Assert.assertEquals("mesaj1", m1.getMessage());
        Assert.assertEquals("mesaj2", m2.getMessage());
        Assert.assertEquals(mr1.getIdMessage(), 1);
        Assert.assertEquals(mr1.getReceiver(), "tu");
    }
}
