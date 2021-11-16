package service;

import domain.User;
import repository.UserRepository;

import java.util.List;

public class UserService {
    UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    /**
     * Adds a user to the repository
     * @param u - the user to be added
     */
    public void save(String firstname, String lastname, String email, String password) {
        repo.save(new User(firstname, lastname, email, password));
    }

    /**
     * Removes a user from the repository
     * @param email - String the email of the user to be removed
     */
    public void remove(String email) {
        repo.remove(email);
    }

    /**
     * Updates a user in the repository
     * @param u - the user with the same email as the user given as parameter
     *          will have their firstname and lastname updated
     */
    public void updateUser(String firstname, String lastname, String email) {
        repo.update(firstname, lastname, email);
    }

    /**
     * @param email - String with the email of the user to be returned
     * @return the user with the email given as a parameter,
     * null if no user in the repository has the given email
     */
    public User getUser(String email) {
        return repo.getUser(email);
    }

    /**
     * @return all the users saved in the repository
     */
    public List<User> getUsers() {
        return repo.getAll();
    }

    /**
     * @return true if the repository has no users saved, false otherwise
     */
    public boolean isEmpty() {
        return repo.isEmpty();
    }

    /**
     * @return the number of users saved in the repository
     */
    public int size() {
        return repo.size();
    }
}
