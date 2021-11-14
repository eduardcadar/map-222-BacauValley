package service;

import domain.FRIENDSHIPSTATE;
import domain.Friendship;
import domain.User;
import repository.FriendshipRepository;

import java.time.LocalDate;
import java.util.List;

public class FriendshipService {
    FriendshipRepository repo;

    public FriendshipService(FriendshipRepository repo) {
        this.repo = repo;
    }

    /**
     * @param email1 - String the email of the first user
     * @param email2 - String the email of the second user
     * @return the friendship of the two users if it is saved in the repository,
     * null otherwise
     */
    public Friendship getFriendship(String email1, String email2) {
        return repo.getFriendship(email1, email2);
    }

    /**
     * Adds a friendship to the repository
     * @param f - the friendship to be added
     */
    public void addFriendship(Friendship f) {
        repo.addFriendship(f);
    }

    /**
     * Removes a friendship from the repository
     * @param f - the friendship to be removed
     */
    public void removeFriendship(Friendship f) {
        repo.removeFriendship(f);
    }

    /**
     * @return all the friendships saved in the repository
     */
    public List<Friendship> getFriendships() {
        return repo.getAll();
    }

    /**
     * @return the number of friendships saved in the repository
     */
    public int size() {
        return repo.size();
    }

    /**
     * @return true if there are no friendships saved, false otherwise
     */
    public boolean isEmpty() {
        return repo.isEmpty();
    }

    /**
     * Removes the friendships of a user
     * @param email - String the email of the user
     */
    public void removeUserFships(String email) {
        repo.removeUserFships(email);
    }

    /**
     * Accepts a friendship
     * @param f - friendship
     * @throws Exception - if there is no pending request in friendship
     */
    public void acceptFriendship(Friendship f) throws Exception {
        if(f.getState() != FRIENDSHIPSTATE.PENDING){
            throw new Exception("There is no pending request between these 2 users");
            // asta nu se mai poate intampla datorita validarilor de mai sus
        }
        repo.acceptFriendship(f);
    }
    // in userInterface()
    // void acceptFriendship(string userEmail){
    //  Friendship f = new Friendship(currentUser.getEmail(), userEmail)
    //  if ( f se gaseste in repo)
    //     srv.acceptFriendship(f)
    // }

    /**
     * @param email - String the email of the user
     * @return list with the emails of a user's friends
     */
    public List<String> getUserFriends(String email) {
        return repo.getUserFriends(email);
    }

    /**
     * Returns a list of emails for friendships in status pending for user with email
     * @param email
     * @return
     */
    public List<String> getUserFriendRequests(String email) {
        return repo.getUserFriendRequests(email);
    }
}
