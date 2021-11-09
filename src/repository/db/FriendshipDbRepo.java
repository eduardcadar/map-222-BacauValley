package repository.db;

import domain.Friendship;
import domain.User;
import repository.FriendshipRepository;
import repository.RepoException;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDbRepo implements FriendshipRepository {
    private final String url;
    private final String username;
    private final String password;
    private final String fshipsTable;
    private final Validator<Friendship> val;

    public FriendshipDbRepo(String url, String username, String password, Validator<Friendship> val, String fshipsTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.val = val;
        this.fshipsTable = fshipsTable;
    }

    @Override
    public void addFriendship(Friendship f) {
        val.validate(f);
        if (getFriendship(f.getFirst(), f.getSecond()) != null)
            throw new RepoException("Cei doi utilizatori sunt deja prieteni");
        String sql = "INSERT INTO " + fshipsTable + " (email1, email2) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, f.getFirst());
            ps.setString(2, f.getSecond());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    public Friendship getFriendship(String email1, String email2) {
        Friendship f = new Friendship(email1, email2);
        String sql = "SELECT FROM " + fshipsTable + " WHERE email1 = ? AND email2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, f.getFirst());
            ps.setString(2, f.getSecond());
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            return f;
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    @Override
    public Friendship getFriendship(User us1, User us2) {
        return getFriendship(us1.getEmail(), us2.getEmail());
    }

    @Override
    public void removeFriendship(Friendship f) {
        if (getFriendship(f.getFirst(), f.getSecond()) == null)
            throw new RepoException("Cei doi utilizatori nu sunt prieteni");
        String sql = "DELETE FROM " + fshipsTable + " WHERE email1 = ? AND email2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, f.getFirst());
            ps.setString(2, f.getSecond());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + fshipsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt("size");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM " + fshipsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public List<Friendship> getAll() {
        List<Friendship> fships = new ArrayList<>();
        String sql = "SELECT * FROM " + fshipsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String email1 = res.getString("email1");
                String email2 = res.getString("email2");
                fships.add(new Friendship(email1, email2));
            }
            return fships;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<String> getUserFriends(String email) {
        List<String> friends = new ArrayList<>();
        for (Friendship f : getAll()) {
            if (f.getFirst().equals(email))
                friends.add(f.getSecond());
            else if (f.getSecond().equals(email))
                friends.add(f.getFirst());
        }
        return friends;
    }

    @Override
    public List<String> getUserFriends(User us) {
        return getUserFriends(us.getEmail());
    }

    @Override
    public void removeUserFships(String email) {
        String sql = "DELETE FROM " + fshipsTable + " WHERE email1 = ? OR email2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    public void removeUserFships(User us) {
        removeUserFships(us.getEmail());
    }
}
