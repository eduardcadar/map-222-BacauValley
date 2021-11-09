package service;

import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.RepoException;
import validator.ValidatorException;

import java.util.List;
import java.util.Map;

public class Service {
    private UserService uSrv;
    private FriendshipService fSrv;
    private Network ntw;

    public Service(UserService uSrv, FriendshipService fSrv, Network ntw) {
        this.uSrv = uSrv;
        this.fSrv = fSrv;
        this.ntw = ntw;
    }

    /**
     * @return numarul de comunitati din retea - int
     */
    public int nrCommunities() {
        return ntw.getNrCommunities();
    }

    /**
     * @return utilizatorii path-ului cel mai lung din reteaua de prietenii - List[User]
     */
    public List<User> getUsersMostFrCom() {
        return ntw.getUsersMostFrCom();
    }

    /**
     * Adauga un utilizator la repository
     * @param u - Userul care va fi adaugat
     * @throws ValidatorException - daca userul nu este valid
     * @throws RepoException - daca email-ul este deja in repository
     */
    public void addUser(User u) throws ValidatorException, RepoException {
        uSrv.save(u);
        ntw.reload();
    }

    /**
     * Sterge un user din repository
     * @param email - String cu email-ul userului care va fi sters
     * @throws RepoException - daca nu exista user salvat cu email-ul introdus ca parametru
     */
    public void removeUser(String email) throws RepoException {
        uSrv.remove(email);
        fSrv.removeUserFships(email);
        ntw.reload();
    }

    /**
     * Returneaza un user
     * @param email - String email-ul utilizatorului care va fi returnat
     * @return utilizatorul care are email-ul introdus ca parametru
     */
    public User getUser(String email) {
        return uSrv.getUser(email);
    }

    /**
     * Adauga o prietenie
     * @param f - prietenia care va fi adaugata
     * @throws RepoException - daca prietenia este deja salvata (cei doi utilizatori sunt deja prieteni)
     */
    public void addFriendship(Friendship f) {
        fSrv.addFriendship(f);
        uSrv.addFriends(f.getFirst(), f.getSecond());
        ntw.reload();
    }

    /**
     * Sterge o prietenie
     * @param f - prietenia care va fi stearsa
     * @throws RepoException - daca prietenia nu este salvata (cei doi utilizatori nu sunt prieteni)
     */
    public void removeFriendship(Friendship f) {
        fSrv.removeFriendship(f);
        uSrv.removeFriends(f.getFirst(), f.getSecond());
        ntw.reload();
    }

    /**
     * Returneaza prietenia dintre doi utilizatori
     * @param email1 - email-ul primului utilizator
     * @param email2 - email-ul celui de-al doilea utilizator
     * @return prietenia dintre cei doi utilizatori
     */
    public Friendship getFriendship(String email1, String email2) {
        return fSrv.getFriendship(email1, email2);
    }

    public void updateUser(User u) {
        uSrv.updateUser(u);
    }

    /**
     * @return dictionar cu utilizatorii din comunitati - Map[Integer, List[String]]
     */
    public Map<Integer, List<String>> getCommunities() {
        return ntw.getCommunities();
    }

    /**
     * Returneaza utilizatorii salvati
     * @return utilizatorii salvati - List[User]
     */
    public List<User> getUsers() {
        return uSrv.getUsers();
    }

    /**
     * Verifica daca repo-ul de utilizatori este gol
     * @return true daca nu sunt utilizatori salvati, false altfel
     */
    public boolean usersIsEmpty() {
        return uSrv.isEmpty();
    }

    /**
     * Returneaza o lista cu toate prieteniile
     * @return lista cu prieteniile salvate - List[Friendship]
     */
    public List<Friendship> getFriendships() {
        return fSrv.getFriendships();
    }

    /**
     * @return numarul de useri salvati in repo - int
     */
    public int usersSize() {
        return uSrv.size();
    }

    /**
     * @return numarul de prietenii din repo - int
     */
    public int friendshipsSize() {
        return fSrv.size();
    }

    /**
     * Verifica daca nu sunt salvate prietenii
     * @return true daca nu sunt salvate prietenii, altfel false
     */
    public boolean friendshipsIsEmpty() {
        return fSrv.isEmpty();
    }
}
