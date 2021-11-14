package domain;

import repository.RepoException;

import java.util.*;

public class User {
    private String firstName, lastName, email;

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
