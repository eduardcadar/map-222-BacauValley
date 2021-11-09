package domain.network;

import domain.User;
import repository.FriendshipRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {
    private final UserRepository uRepo;
    private final FriendshipRepository fRepo;
    private int communitiesNr;
    private final Map<String, Integer> com;
    private MostFriendlyCommunity mfCom;

    public Network(UserRepository usRepo, FriendshipRepository frRepo) {
        this.uRepo = usRepo;
        this.fRepo = frRepo;
        this.com = new HashMap<>();
        reload();
    }

    /**
     * @return utilizatorii path-ului cel mai lung din reteaua de prietenii - List[User]
     */
    public List<User> getUsersMostFrCom() {
        return mfCom.getUsersMostFrCom();
    }

    public void reload() {
        this.communitiesNr = countCommunities();
        this.mfCom = new MostFriendlyCommunity(uRepo, fRepo, com, communitiesNr);
    }

    /**
     * Returneaza un dictionar in care cheia este numarul comunitatii iar valoarea
     * este o lista cu email-urile utilizatorilor din acea comunitate
     * @return dictionar cu utilizatorii din comunitati - Map[Integer, List[String]]
     */
    public Map<Integer, List<String>> getCommunities() {
        Map<Integer, List<String>> comms = new HashMap<>();
        for (int i = 1; i <= communitiesNr; i++)
            comms.put(i, new ArrayList<String>());
        for (User u : uRepo.getAll()) {
            comms.get(com.get(u.getEmail())).add(u.getEmail());
        }
        return comms;
    }

    /**
     * Marcheaza cu true in used toti prietenii unui utilizator (o comunitate)
     * @param e - email-ul utilizatorului
     */
    private void dfs(String e, Integer c) {
        com.put(e, c);
        for (String em : fRepo.getUserFriends(uRepo.getUser(e))) {
            if (com.get(em) == 0)
                dfs(em, c);
        }
    }

    public MostFriendlyCommunity getmfrCom() {
        return mfCom;
    }

    /**
     * Numara comunitatile din retea
     * @return nr de comunitati - int
     */
    private int countCommunities() {
        int nr = 0;
        com.clear();
        for (User u : uRepo.getAll())
            com.put(u.getEmail(), 0);
        for (User u : uRepo.getAll()) {
            if (com.get(u.getEmail()) == 0) {
                nr++;
                dfs(u.getEmail(), nr);
            }
        }
        return nr;
    }

    /**
     * Returneaza numarul de comunitati din retea
     * @return nr comunitati - int
     */
    public int getNrCommunities() {
        return communitiesNr;
    }
}
