package service;

import domain.Friendship;
import repository.FriendshipRepository;

import java.util.List;

public class FriendshipService {
    FriendshipRepository repo;

    public FriendshipService(FriendshipRepository repo) {
        this.repo = repo;
    }

    public Friendship getFriendship(String email1, String email2) {
        return repo.getFriendship(email1, email2);
    }

    public void addFriendship(Friendship f) {
        repo.addFriendship(f);
    }

    public void removeFriendship(Friendship f) {
        repo.removeFriendship(f);
    }

    public List<Friendship> getFriendships() {
        return repo.getAll();
    }

    public int size() {
        return repo.size();
    }

    public boolean isEmpty() {
        return repo.isEmpty();
    }

    public void removeUserFships(String email) {
        repo.removeUserFships(email);
    }
}
