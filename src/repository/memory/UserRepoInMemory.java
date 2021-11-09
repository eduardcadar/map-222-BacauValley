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
     * Adauga un user la repo, daca nu exista deja alt user salvat cu acelasi email
     * @param u - User-ul care va fi adaugat
     * @throws RepoException - daca exista deja un user cu acelasi email
     */
    public void save(User u) {
        if (users.containsKey(u.getEmail()))
            throw new RepoException("Exista deja un utilizator cu acelasi email");
        val.validate(u);
        users.put(u.getEmail(), u);
    }

    @Override
    public void addFriends(String e1, String e2) {
        users.get(e1).addFriend(e2);
        users.get(e2).addFriend(e1);
    }

    @Override
    public void removeFriends(String e1, String e2) {
        users.get(e1).removeFriend(e2);
        users.get(e2).removeFriend(e1);
    }

    @Override
    public void update(User u) {
        if (!users.containsKey(u.getEmail()))
            throw new RepoException("Utilizatorul nu este salvat");
        users.put(u.getEmail(), u);
    }

    /**
     * Returneaza un user dupa email, daca este salvat in repo
     * @param email - String cu email-ul userului
     * @return userul care are email-ul introdus ca parametru - User
     * @throws RepoException - daca email-ul nu este salvat in repo
     */
    public User getUser(String email) throws RepoException {
        if (!users.containsKey(email))
            throw new RepoException("Niciun utilizator salvat cu acest email");
        return users.get(email);
    }

    /**
     * Sterge un user din repo (pe baza email-ului), daca este salvat
     * @param email - email-ul utilizatorului care se sterge
     * @throws RepoException - daca nu exista user salvat cu email-ul primit ca parametru
     */
    public void remove(String email) throws RepoException {
        if (!users.containsKey(email))
            throw new RepoException("Niciun utilizator salvat cu acest email");
        for (String e : users.get(email).getFriends()) {
            users.get(e).removeFriend(email);
        }
        users.remove(email);
    }

    /**
     * @return numarul de useri salvati in repo - int
     */
    @Override
    public int size() {
        return users.size();
    }

    /**
     * Sterge toti userii din repo
     */
    public void clear() {
        users.clear();
    }

    /**
     * Returneaza o lista cu utilizatorii salvati
     * @return lista cu utilizatorii salvati - List[User]
     */
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    /**
     * Verifica daca nu sunt utilizatori salvati
     * @return true daca nu sunt utilizatori salvati, false altfel
     */
    @Override
    public boolean isEmpty() {
        return users.size() == 0;
    }

}
