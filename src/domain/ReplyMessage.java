package domain;

import java.util.List;

public class ReplyMessage extends Message {
    int idMsgRepliedTo;

    public ReplyMessage(String sender, String message, int idMsgRepliedTo) {
        super(sender, message);
        this.idMsgRepliedTo = idMsgRepliedTo;
    }

    public ReplyMessage(String sender, String message, List<String> receivers, int idMsgRepliedTo) {
        super(sender, message, receivers);
        this.idMsgRepliedTo = idMsgRepliedTo;
    }

    public int getIdMsgRepliedTo() {
        return idMsgRepliedTo;
    }

}
