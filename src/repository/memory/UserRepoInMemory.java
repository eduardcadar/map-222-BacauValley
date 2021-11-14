package repository.memory;

import domain.User;
import repository.RepoException;
import repository.UserRepository;
import validator.Validator;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserRepoInMemory implements UserRepository {
    // Map<email, user>
    private Map<String, User> users;
    private Validator<User> val;

    public UserRepoInMemory(Validator<User> val) {
        users = new TreeMap<>();
        this.val = val;
    }

    /**
     * Adds an user to the repo
     * @param u - the user to be added
     * @throws RepoException - if there is already an user saved with the same email
     */
    public void save(User u) {
        if (users.containsKey(u.getEmail()))
            throw new RepoException("There is already an user saved with the same email");
        val.validate(u);
        users.put(u.getEmail(), u);
    }

    /**
     * Updates an user in the memory
     * @param u - the user to be updated
     */
    @Override
    public void update(User u) {
        if (!users.containsKey(u.getEmail()))
            throw new RepoException("The user is not saved");
        users.put(u.getEmail(), u);
    }

    /**
     * Returns an user
     * @param email - String the user's email
     * @return the user with the specified email
     * @throws RepoException - if the user is not saved
     */
    public User getUser(String email) throws RepoException {
        if (!users.containsKey(email))
            throw new RepoException("No user with this email");
        return users.get(email);
    }

    /**
     * Removes an user from the repository
     * @param email - the user's email
     * @throws RepoException - if the user is not saved
     */
    public void remove(String email) throws RepoException {
        if (!users.containsKey(email))
            throw new RepoException("No user with this email");
        users.remove(email);
    }

    /**
     * @return no of users - int
     */
    @Override
    public int size() {
        return users.size();
    }

    /**
     * Removes all the users
     */
    public void clear() {
        users.clear();
    }

    /**
     * @return a list with all the users - List[User]
     */
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    /**
     * @return true if there are no users saved, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return users.size() == 0;
    }

}
