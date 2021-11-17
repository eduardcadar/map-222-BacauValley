package service;

import domain.Message;
import repository.db.MessageDbRepo;

import java.util.List;

public class MessageService {
    MessageDbRepo repo;

    public MessageService(MessageDbRepo repo) {
        this.repo = repo;
    }

    /**
     * Adds a message to the repository
     * @param sender - the email of the message sender
     * @param message - the text of the message
     */
    public void save(String sender, String message) {
        repo.save(new Message(sender, message));
    }

    /**
     * @param id - int id of the message
     * @return the message with the id given, null if no message has id
     */
    public Message getMessage(int id) {
        return repo.getMessage(id);
    }
}
