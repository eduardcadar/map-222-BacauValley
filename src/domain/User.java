package domain;

import repository.RepoException;

import java.util.*;

public class User {
    private String firstName, lastName, email;
    private List<String> friends;

    /**
     * Creates an User object, with the attributes given as parameters
     * @param firstName - String
     * @param lastName - String
     * @param email - String
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        friends = new ArrayList<>();
    }

    /**
     * Returns the first name of an user
     * @return firstName - String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of an user
     * @return lastName - String
     */
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }

    /**
     * Returns the email of an user
     * @return email - String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Adds an user to the list of friends
     * @param us - the user that will be added
     * @throws RepoException - if the user is already in the list of friends
     */
    public void addFriend(User us) {
        addFriend(us.email);
    }

    /**
     * Adds an user to the list of friends
     * @param email - the email of the user that will be added
     * @throws RepoException - if the user is already in the list of friends
     */
    public void addFriend(String email) {
        if (friends.contains(email))
            throw new RepoException("The two users are already friends");
        friends.add(email);
    }

    /**
     * Removes a user from the list of friends
     * @param us - the user that will be removed
     * @throws RepoException - if the user is not in the list of friends
     */
    public void removeFriend(User us) {
        removeFriend(us.email);
    }

    /**
     * Removes a user from the list of friends
     * @param email - the email of the user that will be removed
     * @throws RepoException - if the user is not in the list of friends
     */
    public void removeFriend(String email) {
        if (!friends.contains(email))
            throw new RepoException("The two users are not friends");
        friends.remove(email);
    }

    /**
     * Returns a list with all the friends of an user
     * @return the friends of the user - List[String]
     */
    public List<String> getFriends() {
        return friends;
    }

    /**
     * Removes the user's friends
     */
    public void removeAllFriends() {
        friends.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * Verifies if two users have the same email
     * @param o - Object
     * @return true if they have the same email, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(email, that.email);
    }
}
