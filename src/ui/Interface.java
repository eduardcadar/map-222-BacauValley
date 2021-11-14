package ui;

import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.FriendshipRepository;
import repository.RepoException;
import repository.UserRepository;
import repository.db.DbException;
import repository.db.FriendshipDbRepo;
import repository.db.UserDbRepo;
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
     * File repo
     * @param usersFile - String the name of the users file
     * @param friendshipsFile - String then name of the friendships file
     */
    public Interface(String usersFile, String friendshipsFile) {
        console = new Scanner(System.in);
        Validator<User> uVal = new UserValidator();
        UserRepository uRepo = new UserFileRepo(usersFile, uVal);
        UserService uSrv = new UserService(uRepo);
        Validator<Friendship> fVal = new FriendshipValidator();
        FriendshipRepository fRepo = new FriendshipFileRepo(friendshipsFile, fVal, uRepo);
        FriendshipService fSrv = new FriendshipService(fRepo);
        Network network = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, network);
    }

    /**
     * Database repo
     * @param url - String the url of the database
     */
    public Interface(String url) {
        console = new Scanner(System.in);
        System.out.print("Database username: ");
        String username = console.nextLine();
        System.out.print("Database password: ");
        String password = console.nextLine();
        Validator<User> uVal = new UserValidator();
        UserDbRepo uRepo;
        uRepo = new UserDbRepo(url, username, password, uVal, "users");
        UserService uSrv = new UserService(uRepo);
        Validator<Friendship> fVal = new FriendshipValidator();
        FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, fVal, "friendships");
        FriendshipService fSrv = new FriendshipService(fRepo);
        Network network = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, network);
    }

    /**
     * Shows the main menu
     * @return the input of the user - String
     */
    private String menu() {
        System.out.println("---MENU---");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Number of communities");
        System.out.println("6. Most friendly network");
        System.out.println("7. Update user");
        System.out.println("8. Get user by email");
        System.out.println("9. Get friendship by emails");
        System.out.println("10. Show users");
        System.out.println("11. Show friendships");
        System.out.println("0. Exit");
        System.out.print("Write command: ");
        return console.nextLine().strip();
    }

    /**
     * Starts the program
     */
    public void run() {
        String command;
        while (true) {
            command = menu();
            System.out.println();
            if (command.equals("0"))
                break;
            switch (command) {
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
                    System.out.println("Invalid option");
                    System.out.println();
                }
            }
        }
        System.out.println("You quit");
    }

    /**
     * Verifies if two users are friends
     */
    private void showFriendshipByEmails() {
        System.out.print("Write the email of the first user: ");
        String email1 = console.nextLine().strip();
        System.out.print("Write the email of the second user: ");
        String email2 = console.nextLine().strip();
        Friendship f = srv.getFriendship(email1, email2);
        if (f == null) {
            System.out.println("The two users are not friends");
            return;
        }
        System.out.println(f);
    }

    /**
     * Shows a user
     */
    private void showUserByEmail() {
        System.out.print("Write the user's email: ");
        String email = console.nextLine().strip();
        try {
            User u = srv.getUser(email);
            if (u == null)
                System.out.println("No user saved with this email");
            else System.out.println(u);
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates a user
     */
    private void updateUser() {
        if (srv.usersIsEmpty()) {
            System.out.println("No users saved");
            return;
        }
        Map<Integer, User> users = new HashMap<>();
        Integer i = 0;
        for (User u : srv.getUsers()) {
            i++;
            users.put(i, u);
        }
        System.out.println("----USERS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + users.get(j));
        System.out.print("Write the number of the user you wish to update: ");
        Integer a = console.nextInt();
        console.nextLine();
        System.out.print("Write the new last name of the user: ");
        String lastname = console.nextLine();
        System.out.print("Write the new first name of the user: ");
        String firstname = console.nextLine();
        try {
            srv.updateUser(new User(firstname, lastname, users.get(a).getEmail()));
            System.out.println("The user was updated");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Wrong number");
        }
    }

    /**
     * Shows the users of the longest path in the friendship network
     */
    private void mostFrCommunity() {
        List<User> usrs = srv.getUsersMostFrCom();
        System.out.println("Longest path has a length of " + usrs.size());
        for (User u : usrs) {
            System.out.print("- " + u + " -");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * Shows the communities of the network
     */
    private void communities() {
        Map<Integer, List<String>> comms = srv.getCommunities();
        int nr = srv.nrCommunities();
        if (nr > 1)
            System.out.println("There are " + srv.nrCommunities() + " communities in the network");
        else
            System.out.println("There is only one community in the network");
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
     * Shows all users
     */
    private void showUsers() {
        int i = 1;
        if (srv.usersIsEmpty())
            System.out.println("No users saved");
        else {
            System.out.println("----USERS----");
            for (User us : srv.getUsers()) {
                System.out.println(i + ". " + us);
                i++;
            }
        }
        System.out.println();
    }

    /**
     * Adds a friendship
     */
    private void addFriendship() {
        if (srv.usersSize() < 2) {
            System.out.println("Not enough users saved");
            return;
        }
        Map<Integer, String> users = new HashMap<>();
        Integer i = 0;
        for (User u : srv.getUsers()) {
            i++;
            users.put(i, u.getEmail());
        }
        System.out.println("----USERS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + srv.getUser(users.get(j)));
        System.out.print("Write the numbers of two users for the friendship: ");
        int a = console.nextInt();
        if (a < 1 || a > i) {
            System.out.println("Invalid number");
            return;
        }
        int b = console.nextInt();
        if (b < 1 || b > i) {
            System.out.println("Invalid number");
            return;
        }
        // AM sa presupun ca aici faci validarea ca 2 cei 2 useri sa existe
        // cred ca pute codul, mai degraba o faci in service, ca el
        // are referinte la repo ul de useri si in cauti direct acolo
        console.nextLine();
        try {
            Friendship f = new Friendship(users.get(a), users.get(b));
            srv.addFriendship(f);
            System.out.println("The friendship was added");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes a friendship
     */
    private void removeFriendship() {
        if (srv.friendshipsIsEmpty()) {
            System.out.println("No friendships saved");
            return;
        }
        Map<Integer, Friendship> fships = new HashMap<>();
        Integer i = 0;
        for (Friendship f : srv.getFriendships()) {
            i++;
            fships.put(i, f);
        }
        System.out.println("----FRIENDSHIPS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + fships.get(j));
        System.out.print("Write the number of the friendship you wish to remove: ");
        Integer a = console.nextInt();
        console.nextLine();
        try {
            srv.removeFriendship(fships.get(a));
            System.out.println("The friendship was removed");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Invalid number");
        }
    }

    /**
     * Shows all friendships
     */
    private void showFriendships() {
        if (srv.friendshipsIsEmpty())
            System.out.println("No friendships saved");
        else {
            System.out.println("----FRIENDSHIPS----");
            for (Friendship f : srv.getFriendships())
                System.out.println(f);
        }
        System.out.println();
    }

    /**
     * Removes a user
     */
    private void removeUser() {
        if (srv.usersIsEmpty()) {
            System.out.println("No users saved");
            return;
        }
        Map<Integer, User> users = new HashMap<>();
        Integer i = 0;
        for (User u : srv.getUsers()) {
            i++;
            users.put(i, u);
        }
        System.out.println("----USERS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + users.get(j));
        System.out.print("Write the number of the user you wish to remove: ");
        Integer a = console.nextInt();
        console.nextLine();
        try {
            srv.removeUser(users.get(a).getEmail());
            System.out.println("The user was removed");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Invalid number");
        }
    }

    /**
     * Adds a user
     */
    private void addUser() {
        String firstname, lastname, email;
        System.out.print("Write the first name of the user: ");
        firstname = console.nextLine();
        System.out.print("Write the last name of the user: ");
        lastname = console.nextLine();
        System.out.print("Write the email of the user: ");
        email = console.nextLine();
        try {
            srv.addUser(new User(lastname, firstname, email));
            System.out.println("The user was added");
        } catch (ValidatorException | DbException | RepoException e) {
            System.out.println(e.getMessage());
        }
    }
}
