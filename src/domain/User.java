package domain;

import repository.RepoException;

import java.util.*;

public class User {
    private String firstName, lastName, email;
    private List<String> friends;

    /**
     * Creeaza un obiect de tip User, cu atributele data ca parametrii
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
     * Returneaza prenumele unui user
     * @return firstName - String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returneaza numele de familie al unui user
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
     * Returneaza emailul unui user
     * @return email - String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Adauga un utilizator in lista de prieteni
     * @param us - utilizatorul care va fi adaugat
     * @throws RepoException - daca utilizatorul este deja in lista de prieteni
     */
    public void addFriend(User us) {
        if (friends.contains(us.getEmail()))
            throw new RepoException("Cei doi utilizatori sunt deja prieteni");
        friends.add(us.getEmail());
    }

    public void addFriend(String email) {
        if (friends.contains(email))
            throw new RepoException("Cei doi utilizatori sunt deja prieteni");
        friends.add(email);
    }

    /**
     * Sterge un utilizator din lista de prieteni
     * @param us - utilizatorul care va fi sters
     * @throws RepoException - daca utilizatorul nu este in lista de prieteni
     */
    public void removeFriend(User us) {
        if (!friends.contains(us.getEmail()))
            throw new RepoException("Cei doi utilizatori nu sunt prieteni");
        friends.remove(us.getEmail());
    }

    public void removeFriend(String email) {
        if (!friends.contains(email))
            throw new RepoException("Cei doi utilizatori nu sunt prieteni");
        friends.remove(email);
    }

    /**
     * Returneaza o lista cu toti prietenii utilizatorului
     * @return prietenii utilizatorului - List[String]
     */
    public List<String> getFriends() {
        return friends;
    }

    /**
     * Sterge toti prietenii utilizatorului
     */
    public void removeAllFriends() {
        friends.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * Verifica daca doua variabile refera acelasi User
     * @param o - Object
     * @return true daca refera acelasi user, false altfel
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(email, that.email);
    }
}
