package service;

import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.RepoException;
import validator.ValidatorException;

import java.util.List;
import java.util.Map;

public class Service {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final Network network;

    public Service(UserService userService, FriendshipService friendshipService, Network network) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.network = network;
    }

    /**
     * @return no of communities - int
     */
    public int nrCommunities() {
        return network.getNrCommunities();
    }

    /**
     * @return the users of the longest path in the friendships network - List[User]
     */
    public List<User> getUsersMostFrCom() {
        return network.getUsersMostFrCom();
    }

    /**
     * Adds an user
     * @param u - the user to be added
     * @throws ValidatorException - if the user is not valid
     * @throws RepoException - if the email is already saved
     */
    public void addUser(User u) throws ValidatorException, RepoException {
        userService.save(u);
    }

    /**
     * Removes an user
     * @param email - String the email of the user to be removed
     * @throws RepoException - if there's no user with the given email
     */
    public void removeUser(String email) {
        userService.remove(email);
        friendshipService.removeUserFships(email);
    }

    /**
     * Returns an user
     * @param email - String the email of the user
     * @return the user with the given email
     */
    public User getUser(String email) {
        return userService.getUser(email);
    }

    /**
     * Adds a friendship
     * @param f - the friendship to be added
     * @throws RepoException - if the friendship is already saved
     */
    public void addFriendship(Friendship f) {
        friendshipService.addFriendship(f);
    }

    /**
     * Removes a friendship
     * @param f - the friendship to be removed
     * @throws RepoException - if the friendship is not saved
     */
    public void removeFriendship(Friendship f) {
        friendshipService.removeFriendship(f);
    }

    /**
     * Returns the friendship of two users
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     * @return the friendship of the two users
     */
    public Friendship getFriendship(String email1, String email2) {
        return friendshipService.getFriendship(email1, email2);
    }

    /**
     * Updates an user
     * @param u - the new user with that email
     */
    public void updateUser(User u) {
        userService.updateUser(u);
    }

    /**
     * @return dictionary with the users of the communites - Map[Integer, List[String]]
     */
    public Map<Integer, List<String>> getCommunities() {
        return network.getCommunities();
    }

    /**
     * @return saved users - List[User]
     */
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * @return true if there are no users saved, false otherwise
     */
    public boolean usersIsEmpty() {
        return userService.isEmpty();
    }

    /**
     * @return saved friendships - List[Friendship]
     */
    public List<Friendship> getFriendships() {
        return friendshipService.getFriendships();
    }

    /**
     * @return no of users - int
     */
    public int usersSize() {
        return userService.size();
    }

    /**
     * @return no of friendships - int
     */
    public int friendshipsSize() {
        return friendshipService.size();
    }

    /**
     * @return true if there are no friendships saved, false otherwise
     */
    public boolean friendshipsIsEmpty() {
        return friendshipService.isEmpty();
    }
}
