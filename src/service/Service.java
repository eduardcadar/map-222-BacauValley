package service;

import Utils.UserFriendDTO;
import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.RepoException;
import validator.ValidatorException;

import java.util.ArrayList;
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
     * Adds a user
     * @param firstname - the first name of the user
     * @param lastname - the last name of the user
     * @param email - the email of the user
     * @param password - the password of the user
     * @throws ValidatorException - if the user is not valid
     * @throws RepoException - if the email is already saved
     */
    public void addUser(String firstname, String lastname, String email, String password) throws ValidatorException, RepoException {
        userService.save(firstname, lastname, email, password);
    }

    /**
     * Removes a user
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
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     * @throws RepoException - if the friendship is already saved
     */
    public void addFriendship(String email1, String email2) {
        friendshipService.addFriendship(email1, email2);
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
     * Updates a user
     * @param firstname - the new first name of the user
     * @param lastname - the new last name of the user
     * @param email - the email of the user to be updated
     * @param password - the new password of the user
     */
    public void updateUser(String firstname, String lastname, String email, String password) {
        userService.updateUser(firstname, lastname, email, password);
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

    /**
     * Accepts the friendship setting its status to approved and setting the date
     * @param f - friendship
     */
    public void acceptFriendship(Friendship f)  {
        friendshipService.acceptFriendship(f);
    }
    /**
     * @param email - String the email of the user
     * @return the friends of the user
     */
    public List<User> getUserFriends(String email) {
        List<User> friends = new ArrayList<>();
        List<String> friendsEmails = friendshipService.getUserFriends(email);
        for (String friendEmail : friendsEmails) {
            friends.add(userService.getUser(friendEmail));
        }
        return friends;
    }
    // instead of public List<User>  getUserFriends
    // TODO
    // - return a list of DOTS where you have User and FriendsShipObject
    public List<UserFriendDTO> getFriendshipsDTO(String email){
        List<UserFriendDTO> userFriendDTOS  = new ArrayList<>();
        List<String> friendsEmail = friendshipService.getUserFriends(email);
        for(String friendEmail : friendsEmail){
            Friendship friendship = friendshipService.getFriendship(email, friendEmail);
            User friend;
            if(email.equals(friendship.getFirst())) {
                 friend = userService.getUser(friendship.getSecond());
            }
            else{
                 friend = userService.getUser(friendship.getFirst());
            }
            UserFriendDTO userFriendDTO = new UserFriendDTO(friend.getFirstName(), friend.getLastName(), friendship.getDate());
            userFriendDTOS.add(userFriendDTO);
        }
        return userFriendDTOS;
    }

    /**
     * @param email - String the email of the user
     * @return the users that are not friends with the given user
     */
    public List<User> getNotFriends(String email) {
        List<String> friends = friendshipService.getUserFriendsAll(email);
        List<User> notFriends = new ArrayList<>();

        for (User u : userService.getUsers())
            if (!friends.contains(u.getEmail()) && u.getEmail().compareTo(email) != 0)
                notFriends.add(u);

        return notFriends;
    }

    /**
     * Returns a list of a pending friend requests for the user with email
     * @param email - string
     * @return - List
     */
    public List<User> getUserFriendRequests(String email) {
        ArrayList<User> users = new ArrayList<>();
        for(String friendEmail: friendshipService.getUserFriendRequests(email)){
            users.add(userService.getUser(friendEmail));
        }
        return users;
    }
}
