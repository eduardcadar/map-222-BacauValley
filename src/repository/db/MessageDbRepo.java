package repository.db;

import domain.Message;
import domain.ReplyMessage;
import validator.MessageValidator;
import validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class MessageDbRepo {
    private final String url, username, password, messagesTable;
    private final Validator<Message> validator;

    public MessageDbRepo(String url, String username, String password, MessageValidator validator, String messagesTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messagesTable = messagesTable;
        this.validator = validator;
        String sql = "CREATE TABLE IF NOT EXISTS " + messagesTable +
                "(id serial, " +
                " sender varchar NOT NULL," +
                " messageText varchar NOT NULL," +
                " sentDate varchar NOT NULL," +
                " idMsgRepliedTo int DEFAULT NULL," +
                " PRIMARY KEY (id)," +
                " FOREIGN KEY (sender) REFERENCES users (email)," +
                " FOREIGN KEY (idMsgRepliedTo) REFERENCES messages (id)" +
                ");" +
                " CREATE UNIQUE index IF NOT EXISTS " + messagesTable + "_id_uindex ON " +
                messagesTable + " (id);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Validates and saves a message in the database
     * @param message - the message to be saved
     */
    public void save(Message message) {
        validator.validate(message);
        String sql = "INSERT INTO " + messagesTable + " (sender, messageText, sentDate) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, message.getSender());
            ps.setString(2, message.getMessage());
            ps.setString(3, String.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Validates and saves a reply message in the database
     * @param message - the message to be saved
     */
    public void save(ReplyMessage message) {
        validator.validate(message);
        String sql = "INSERT INTO " + messagesTable + " (sender, messageText, sentDate,  idMsgRepliedTo) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, message.getSender());
            ps.setString(2, message.getMessage());
            ps.setString(3, String.valueOf(LocalDateTime.now()));
            ps.setInt(4, message.getIdMsgRepliedTo());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @param id - int - the id of the message to be returned
     * @return the message with the specified id,
     * null if no message has the given id
     */
    public Message getMessage(int id) {
        String sql = "SELECT * FROM " + messagesTable + " WHERE id = ?";
        Message message;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            message = new Message(res.getString("sender"), res.getString("messageText"));
            return message;
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @return int - the number of messages saved in the database
     */
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + messagesTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt("size");
            }
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return 0;
    }

    /**
     * Removes all messages from database
     */
    public void clear() {
        String sql = "DELETE FROM " + messagesTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }
}