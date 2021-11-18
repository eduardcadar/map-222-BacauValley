package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private int ID;
    private final String sender, message;
    private List<String> receivers;

    private LocalDateTime date;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.date = null;
    }

    public Message(String sender, String message, List<String> receivers) {
        this.sender = sender;
        this.message = message;
        this.receivers = receivers;
        this.date = null;
    }

    /**
     * Sets the list of receivers of the message
     * @param receivers - list with the emails of the receivers
     */
    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    /**
     * Sets the date of the message
     * @param date - LocalDateTime
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * @return returns the sender of the message
     */
    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }

    /**
     * @return returns the text of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return returns the date and time of the message
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @return list with the message receivers
     */
    public List<String> getReceivers() {
        return receivers;
    }

    /**
     * @return id of the message
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets the message id
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }
}
