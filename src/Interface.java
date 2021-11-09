import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.FriendshipRepository;
import repository.RepoException;
import repository.UserRepository;
import repository.db.DbException;
import repository.db.FriendshipDbRepo;
import repository.db.UserDbRepo;
import repository.FriendshipsInitializer;
import repository.file.FriendshipFileRepo;
import repository.file.UserFileRepo;
import service.FriendshipService;
import service.Service;
import service.UserService;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;
import validator.ValidatorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Interface {
    private final Scanner console;
    private final Service srv;

    /**
     * Constructor pentru repo in fisiere
     * @param usersFile - String numele fisierului in care se salveaza utilizatorii
     * @param friendshipsFile - String numele fisierului in care se salveaza prieteniile
     */
    public Interface(String usersFile, String friendshipsFile) {
        console = new Scanner(System.in);
        Validator<User> uVal = new UserValidator();
        UserRepository uRepo = new UserFileRepo(usersFile, uVal);
        UserService uSrv = new UserService(uRepo);
        Validator<Friendship> fVal = new FriendshipValidator();
        FriendshipRepository fRepo = new FriendshipFileRepo(friendshipsFile, fVal, uRepo);
        FriendshipService fSrv = new FriendshipService(fRepo);
        FriendshipsInitializer FI = new FriendshipsInitializer(uRepo, fRepo);
        Network ntw = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, ntw);
    }

    /**
     * Constructor pentru repo in baza de date
     * @param url - String url-ul bazei de date
     */
    public Interface(String url) {
        console = new Scanner(System.in);
        System.out.print("Introduceti username-ul bazei de date: ");
        String username = console.nextLine();
        System.out.print("Introduceti parola bazei de date: ");
        String password = console.nextLine();
        Validator<User> uVal = new UserValidator();
        UserDbRepo uRepo;
        uRepo = new UserDbRepo(url, username, password, uVal, "users");
        UserService uSrv = new UserService(uRepo);
        Validator<Friendship> fVal = new FriendshipValidator();
        FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, fVal, "friendships");
        FriendshipService fSrv = new FriendshipService(fRepo);
        Network ntw = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, ntw);
    }

    /**
     * Afiseaza meniul principal
     * @return comanda introdusa - String
     */
    private String meniu() {
        System.out.println("---MENIU PRINCIPAL---");
        System.out.println("1. Adauga utilizator");
        System.out.println("2. Sterge utilizator");
        System.out.println("3. Adauga prieten");
        System.out.println("4. Sterge prieten");
        System.out.println("5. Numar comunitati");
        System.out.println("6. Cea mai sociabila comunitate");
        System.out.println("7. Actualizeaza utilizator");
        System.out.println("8. Afisati utilizatorul dupa email");
        System.out.println("9. Afisati prietenia dupa email-uri");
        System.out.println("10. Afiseaza utilizatorii salvati");
        System.out.println("11. Afiseaza prieteniile salvate");
        System.out.println("0. Inchide programul");
        System.out.print("Introduceti optiunea dorita: ");
        return console.nextLine().strip();
    }

    /**
     * Porneste executia programului
     */
    public void run() {
        String com;
        while (true) {
            com = meniu();
            System.out.println();
            if (com.compareTo("0") == 0) break;
            switch (com) {
                case "1" -> addUser();
                case "2" -> removeUser();
                case "3" -> addFriendship();
                case "4" -> removeFriendship();
                case "5" -> communities();
                case "6" -> mostFrCommunity();
                case "7" -> updateUser();
                case "8" -> showUserByEmail();
                case "9" -> showFriendshipByEmails();
                case "10" -> showUsers();
                case "11" -> showFriendships();
                default -> {
                    System.out.println("Optiune invalida");
                    System.out.println();
                }
            }
        }
        System.out.println("Ati iesit din program");
    }

    /**
     * Verifica daca doi utilizatori sunt prieteni
     */
    private void showFriendshipByEmails() {
        System.out.print("Introduceti email-ul primului utilizator: ");
        String email1 = console.nextLine().strip();
        System.out.print("Introduceti email-ul celui de-al doilea utilizator: ");
        String email2 = console.nextLine().strip();
        Friendship f = srv.getFriendship(email1, email2);
        if (f == null) {
            System.out.println("Cei doi utilizatori nu sunt prieteni");
            return;
        }
        System.out.println(f);
    }

    /**
     * Afiseaza un utilizator; se va introduce email-ul acestuia
     */
    private void showUserByEmail() {
        System.out.print("Introduceti email-ul utilizatorului: ");
        String email = console.nextLine().strip();
        try {
            User u = srv.getUser(email);
            if (u == null)
                System.out.println("Utilizatorul nu este salvat");
            else System.out.println(u);
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Actualizeaza un utilizator
     */
    private void updateUser() {
        if (srv.usersIsEmpty()) {
            System.out.println("Nu sunt utilizatori salvati");
            return;
        }
        Map<Integer, User> users = new HashMap<>();
        Integer i = 0;
        for (User u : srv.getUsers()) {
            i++;
            users.put(i, u);
        }
        System.out.println("----UTILIZATORI----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + users.get(j));
        System.out.print("Introduceti numarul utilizatorului pe care doriti sa il actualizati: ");
        Integer a = console.nextInt();
        console.nextLine();
        System.out.print("Introduceti noul nume de familie al utilizatorului: ");
        String lastname = console.nextLine();
        System.out.print("Introduceti noul prenume al utilizatorului: ");
        String firstname = console.nextLine();
        try {
            srv.updateUser(new User(firstname, lastname, users.get(a).getEmail()));
            System.out.println("Utilizatorul a fost actualizat");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Nu ati introdus un numar valid");
        }
    }

    /**
     * Afiseaza utilizatorii drumului cel mai lung din reteaua de prietenii
     */
    private void mostFrCommunity() {
        List<User> usrs = srv.getUsersMostFrCom();
        System.out.println("Cel mai lung drum are lungimea " + usrs.size());
        for (User u : usrs) {
            System.out.print("- " + u + " -");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * Afiseaza comunitatile din retea
     */
    private void communities() {
        Map<Integer, List<String>> comms = srv.getCommunities();
        int nr = srv.nrCommunities();
        if (nr > 1)
            System.out.println("In retea sunt " + srv.nrCommunities() + " comunitati");
        else
            System.out.println("In retea este o singura comunitate");
        List<String> usrs;
        for (int j = 1; j <= nr; j++) {
            usrs = comms.get(j);
            for (String e : usrs) {
                System.out.print("- " + srv.getUser(e) + " -");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Afiseaza utilizatorii salvati
     */
    private void showUsers() {
        int i = 1;
        if (srv.usersIsEmpty())
            System.out.println("Nu sunt utilizatori salvati");
        else {
            System.out.println("----UTILIZATORI----");
            for (User us : srv.getUsers()) {
                System.out.println(i + ". " + us);
                i++;
            }
        }
        System.out.println();
    }

    /**
     * Adauga o prietenie
     */
    private void addFriendship() {
        if (srv.usersSize() < 2) {
            System.out.println("Nu sunt destui utilizatori salvati");
            return;
        }
        Map<Integer, String> usrs = new HashMap<>();
        Integer i = 0;
        for (User u : srv.getUsers()) {
            i++;
            usrs.put(i, u.getEmail());
        }
        System.out.println("----UTILIZATORI----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + srv.getUser(usrs.get(j)));
        System.out.print("Introduceti numerele a doi utilizatori pentru prietenie: ");
        int a = console.nextInt();
        if (a < 1 || a > i) {
            System.out.println("Nu ati introdus un numar valid");
            return;
        }
        int b = console.nextInt();
        if (b < 1 || b > i) {
            System.out.println("Nu ati introdus un numar valid");
            return;
        }
        console.nextLine();
        try {
            Friendship f = new Friendship(srv.getUser(usrs.get(a)), srv.getUser(usrs.get(b)));
            srv.addFriendship(f);
            System.out.println("Prietenia a fost adaugata");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sterge o prietenie
     */
    private void removeFriendship() {
        if (srv.friendshipsIsEmpty()) {
            System.out.println("Nu sunt prietenii salvate");
            return;
        }
        Map<Integer, Friendship> fships = new HashMap<>();
        Integer i = 0;
        for (Friendship f : srv.getFriendships()) {
            i++;
            fships.put(i, f);
        }
        System.out.println("----PRIETENII----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + fships.get(j));
        System.out.print("Introduceti numarul prieteniei pe care doriti sa o stergeti: ");
        Integer a = console.nextInt();
        console.nextLine();
        try {
            srv.removeFriendship(fships.get(a));
            System.out.println("Prietenia a fost stearsa");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Nu ati introdus un numar valid");
        }
    }

    private void showFriendships() {
        if (srv.friendshipsIsEmpty())
            System.out.println("Nu sunt prietenii salvate");
        else {
            System.out.println("----PRIETENII----");
            for (Friendship f : srv.getFriendships())
                System.out.println(f);
        }
        System.out.println();
    }

    /**
     * Sterge un utilizator salvat
     */
    private void removeUser() {
        if (srv.usersIsEmpty()) {
            System.out.println("Nu sunt utilizatori salvati");
            return;
        }
        Map<Integer, User> users = new HashMap<>();
        Integer i = 0;
        for (User u : srv.getUsers()) {
            i++;
            users.put(i, u);
        }
        System.out.println("----UTILIZATORI----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + users.get(j));
        System.out.print("Introduceti numarul utilizatorului pe care doriti sa il stergeti: ");
        Integer a = console.nextInt();
        console.nextLine();
        try {
            srv.removeUser(users.get(a).getEmail());
            System.out.println("Utilizatorul a fost sters");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Nu ati introdus un numar valid");
        }
    }

    /**
     * Adauga un utilizator
     */
    private void addUser() {
        String nume, prenume, email;
        System.out.print("Introduceti numele utilizatorului: ");
        nume = console.nextLine();
        System.out.print("Introduceti prenumele utilizatorului: ");
        prenume = console.nextLine();
        System.out.print("Introduceti email-ul utilizatorului: ");
        email = console.nextLine();
        try {
            srv.addUser(new User(prenume, nume, email));
            System.out.println("Userul a fost adaugat!");
        } catch (ValidatorException | DbException | RepoException e) {
            System.out.println(e.getMessage());
        }
    }

}
