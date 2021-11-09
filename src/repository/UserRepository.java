package repository;

import domain.User;

import java.util.List;

public interface UserRepository {

    public void save(User u) throws RepoException;
    public User getUser(String email) throws RepoException;
    public void remove(String email) throws RepoException;
    public int size();
    public void clear();
    public List<User> getAll();
    public boolean isEmpty();
    public void addFriends(String email1, String email2);
    public void removeFriends(String email1, String email2);
    public void update(User u);
}
