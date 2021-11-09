package repository;

import domain.Friendship;
import domain.User;

import java.util.List;

public interface FriendshipRepository {

    public void addFriendship(Friendship f);
    public void removeFriendship(Friendship f);
    public int size();
    public void clear();
    public boolean isEmpty();
    public List<Friendship> getAll();
    public List<String> getUserFriends(String email);
    public List<String> getUserFriends(User us);
    public void removeUserFships(String email);
    public Friendship getFriendship(String email1, String email2);
    public Friendship getFriendship(User us1, User us2);
}
