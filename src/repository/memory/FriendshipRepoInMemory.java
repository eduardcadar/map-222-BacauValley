package repository.memory;

import domain.Friendship;
import domain.User;
import repository.FriendshipRepository;
import repository.RepoException;
import repository.UserRepository;
import validator.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipRepoInMemory implements FriendshipRepository {
    private final UserRepository userRepo;
    private final Set<Friendship> friendships;
    private final Validator<Friendship> val;

    public FriendshipRepoInMemory(Validator<Friendship> val, UserRepository userRepo) {
        this.userRepo = userRepo;
        friendships = new HashSet<>();
        this.val = val;
    }

    /**
     * Adauga o prietenie la repo
     * @param f - prietenia care va fi adaugata
     * @throws RepoException - daca prietenia este deja in repo
     */
    @Override
    public void addFriendship(Friendship f) throws RepoException {
        val.validate(f);
        if (!friendships.add(f))
            throw new RepoException("Cei doi utilizatori sunt deja prieteni");
    }

    /**
     * Sterge o prietenie din repo
     * @param f - prietenia care va fi stearsa
     * @throws RepoException - daca prietenia nu este in repo
     */
    @Override
    public void removeFriendship(Friendship f) throws RepoException {
        val.validate(f);
        if (!friendships.remove(f))
            throw new RepoException("Cei doi utilizatori nu sunt prieteni");
    }

    /**
     * @return numarul de prietenii din repo - int
     */
    @Override
    public int size() {
        return friendships.size();
    }

    /**
     * Sterge toate prieteniile din repo
     */
    @Override
    public void clear() {
        for (User e : userRepo.getAll())
            e.removeAllFriends();
        friendships.clear();
    }

    /**
     * Verifica daca nu sunt salvate prietenii
     * @return true daca nu sunt salvate prietenii, altfel false
     */
    @Override
    public boolean isEmpty() {
        return friendships.size() == 0;
    }

    /**
     * Sterge toate prieteniile unui utilizator
     * @param email - email-ul utilizatorului
     */
    public void removeUserFships(String email) {
        List<Friendship> fships = new ArrayList<>();
        for (Friendship f : friendships) {
            if (f.getFirst().equals(email) || f.getSecond().equals(email))
                fships.add(f);
        }
        for (Friendship f : fships) {
            removeFriendship(f);
        }
    }

    @Override
    public Friendship getFriendship(String email1, String email2) {
        Friendship f = new Friendship(email1, email2);
        if (friendships.contains(f))
            return f;
        return null;
    }

    @Override
    public Friendship getFriendship(User us1, User us2) {
        return getFriendship(us1.getEmail(), us2.getEmail());
    }

    /**
     * Returneaza o lista cu toate prieteniile
     * @return lista cu prieteniile salvate - List[Friendship]
     */
    @Override
    public List<Friendship> getAll() {
        return friendships.stream().toList();
    }

    @Override
    public List<String> getUserFriends(String email) {
        List<String> friends = new ArrayList<>();
        for (Friendship f : friendships) {
            if (f.getFirst().equals(email))
                friends.add(f.getSecond());
            if (f.getSecond().equals(email))
                friends.add(f.getFirst());
        }
        return friends;
    }

    @Override
    public List<String> getUserFriends(User us) {
        return getUserFriends(us.getEmail());
    }
}
