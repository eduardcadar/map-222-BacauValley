package service;

import domain.User;
import repository.UserRepository;

import java.util.List;

public class UserService {
    UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void save(User u) {
        repo.save(u);
    }

    public void remove(String email) {
        repo.remove(email);
    }

    public void addFriends(String email1, String email2) {
        repo.addFriends(email1, email2);
    }

    public void removeFriends(String email1, String email2) {
        repo.removeFriends(email1, email2);
    }

    public void updateUser(User u) {
        repo.update(u);
    }

    public User getUser(String email) {
        return repo.getUser(email);
    }

    public List<User> getUsers() {
        return repo.getAll();
    }

    public boolean isEmpty() {
        return repo.isEmpty();
    }

    public int size() {
        return repo.size();
    }
}
