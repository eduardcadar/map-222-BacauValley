package repository.db;

import domain.User;
import repository.RepoException;
import repository.UserRepository;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDbRepo implements UserRepository {
    private final String url;
    private final String username;
    private  String password;
    private final String usersTable;
    private final Validator<User> val;

    public UserDbRepo(String url, String username, String password, Validator<User> val, String usersTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.val = val;
        this.usersTable = usersTable;
        String sql = "CREATE TABLE IF NOT EXISTS " + usersTable +
                "(firstname varchar NOT NULL," +
                " lastname varchar NOT NULL, " +
                " email varchar NOT NULL, " +
                " PRIMARY KEY (email) " +
                ")";
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
             ps.executeUpdate();
        }

        catch (SQLException e) {
            // daca facem o singura conexiune si in functie de cum se arunca exceptie pun la password - postgres
            this.password = "postgres";
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    @Override
    public void save(User u) {
        val.validate(u);
        if (getUser(u.getEmail()) != null)
            throw new RepoException("Exista deja un utilizator cu acest email");
        String sql = "INSERT INTO " + usersTable + " (firstname, lastname, email) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getFirstName());
            ps.setString(2, u.getLastName());
            ps.setString(3, u.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public User getUser(String email) {
        String sql = "SELECT * FROM " + usersTable + " WHERE email = ?";
        User us;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            us = new User(res.getString("firstname"), res.getString("lastname"), res.getString("email"));
            return us;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void remove(String email) {
        getUser(email);
        String sql = "DELETE FROM " + usersTable + " WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + usersTable;
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
        String sql = "DELETE FROM " + usersTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + usersTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            //DE CONTINUAT
            while (res.next()) {
                String firstname = res.getString("firstname");
                String lastname = res.getString("lastname");
                String email = res.getString("email");
                users.add(new User(firstname, lastname, email));
            }
            return users;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void addFriends(String email1, String email2) {

    }

    @Override
    public void removeFriends(String email1, String email2) {

    }

    @Override
    public void update(User u) {
        if (getUser(u.getEmail()) == null)
            throw new RepoException("Utilizatorul nu este salvat");
        String sql = "UPDATE " + usersTable + " SET firstname = ?, lastname = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getFirstName());
            ps.setString(2, u.getLastName());
            ps.setString(3, u.getEmail());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
