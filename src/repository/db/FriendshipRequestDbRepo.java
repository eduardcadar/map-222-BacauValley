package repository.db;

import domain.Friendship;
import domain.FriendshipRequest;
import domain.REQUESTSTATE;
import repository.FriendshipRepository;
import repository.FriendshipRequestRepository;
import repository.RepoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRequestDbRepo implements FriendshipRequestRepository {
    private String url;
    private String username;
    private String password;
    private String tableName;

    public FriendshipRequestDbRepo(String url, String username, String password, String tableName){
        this.url = url;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName +
                "(email1 varchar," +
                " email2 varchar," +
                " requeststate varchar DEFAULT 'PENDING'," +
                " PRIMARY KEY (email1,email2)," +
                " FOREIGN KEY (email1) references users(email) ON DELETE CASCADE," +
                " FOREIGN KEY (email2) references users(email) ON DELETE CASCADE" +
                ")";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }

    }

    public void addRequest(FriendshipRequest request) {
        if(getRequest(request.getFirst(), request.getSecond()) != null){
            throw new RepoException("There is already a request send by user");
        }
        String sql = "INSERT INTO " + tableName + " (email1, email2, requeststate) values (?, ?, ?) ";
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, request.getFirst());
            ps.setString(2, request.getSecond());
            ps.setString(3, request.getState().toString());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }

    }

    public void clear() {
        String sql = "DELETE FROM " + tableName;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    public int size() {
        return getAll().size();
    }

    public List<FriendshipRequest> getAll() {
        ArrayList<FriendshipRequest> friendshipRequests = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                String requestEmail1 = resultSet.getString("email1");
                String requestEmail2 = resultSet.getString("email2");
                REQUESTSTATE requestState = REQUESTSTATE.valueOf(resultSet.getString("requeststate"));
                friendshipRequests.add(new FriendshipRequest(requestEmail1,requestEmail2,requestState));
            }
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return friendshipRequests;
    }

    public FriendshipRequest getRequest(String email1, String email2) {
        String sql = "SELECT * FROM " + tableName + " WHERE (email1 = ? AND email2 = ?)";
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email1);
            ps.setString(2, email2);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                String requestEmail1 = resultSet.getString("email1");
                String requestEmail2 = resultSet.getString("email2");
                REQUESTSTATE requestState = REQUESTSTATE.valueOf(resultSet.getString("requeststate"));
                return new FriendshipRequest(requestEmail1,requestEmail2,requestState);
            }
            else
                return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    public void removeRequest(FriendshipRequest friendshipRequest) {
        if(getRequest(friendshipRequest.getFirst(),friendshipRequest.getSecond()) == null)
            throw new RepoException("Friendship request doesn't exists");
        String sql = "DELETE FROM " + tableName + " WHERE (email1 = ? AND email2 = ?) ";
        try(Connection connection = DriverManager.getConnection(url,username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, friendshipRequest.getFirst());
            ps.setString(2, friendshipRequest.getSecond());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }

    }

    public boolean isEmpty() {
        return getAll().isEmpty();
    }

    @Override
    public void update(FriendshipRequest request) {
        String sql = "UPDATE " + tableName +
                " SET requeststate = ?" +
                " WHERE (email1 = ? AND email2 = ?)";
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, request.getState().toString());
            ps.setString(2, request.getFirst());
            ps.setString(3, request.getSecond());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Get a list of requests received by user with email equal with param email
     * @param email - String
     * @return - List of strings
     */
    @Override
    public List<String> getUserFriendRequests(String email) {
        ArrayList<String> friends = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE email2 = ? AND requeststate = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, "PENDING");
            ResultSet res = ps.executeQuery();
            while(res.next()) {
                String emailRequest = res.getString("email1");
                friends.add(emailRequest);
            }
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return friends;
    }
}
