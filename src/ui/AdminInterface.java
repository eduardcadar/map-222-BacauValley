package ui;

import domain.Friendship;
import domain.User;
import repository.RepoException;
import repository.db.DbException;
import service.Service;
import validator.ValidatorException;

import java.util.*;

public class AdminInterface implements UserInterface {
    private final Scanner console;
    private final Service srv;

    public AdminInterface(Scanner console, Service srv) {
        this.console = console;
        this.srv = srv;
    }

    /**
     * Shows the main menu
     * @return the input of the user - String
     */
    private String menu() {
        System.out.println("---MENU---");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Number of communities");
        System.out.println("4. Most friendly network");
        System.out.println("5. Get user by email");
        System.out.println("6. Get friendship by emails");
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
                case "3" -> communities();
                case "4" -> mostFrCommunity();
                case "5" -> showUserByEmail();
                case "6" -> showFriendshipByEmails();
                case "10" -> showUsers();
                case "11" -> showFriendships();
                default -> {
                    System.out.println("Invalid option");
                    System.out.println();
                }
            }
        }
        System.out.println("Exiting admin interface...");
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
    private Map<Integer, User> showUsers() {
        Map<Integer, User> users = new HashMap<>();

        int i = 1;
        if (srv.usersIsEmpty())
            System.out.println("No users saved");
        else {
            System.out.println("----USERS----");
            for (User us : srv.getUsers()) {
                users.put(i, us);
                System.out.println(i + ". " + us);
                i++;
            }
        }
        System.out.println();
        return users;
    }

    /**
     * Shows all friendships
     */
    private void showFriendships() {
        List<Friendship> friendships = srv.getFriendships();
        if (friendships.size() == 0)
            System.out.println("No friendships saved");
        else {
            System.out.println("----FRIENDSHIPS----");
            for (Friendship f : friendships)
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
        Map<Integer, User> users = showUsers();
        System.out.print("Write the number of the user you wish to remove: ");
        Integer nrOfUser;
        try {
            nrOfUser = console.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Wrong input");
            return;
        } finally {
            console.nextLine();
        }
        try {
            srv.removeUser(users.get(nrOfUser).getEmail());
            System.out.println("The user was removed");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Invalid number");
        }
    }

    /**
     * Verifies the password
     * @param password - String - the password to be verified
     * @return true if the password is good, false otherwise
     */
    private boolean verifyPassword(String password) {
        return password.length() > 5 && password.length() < 64;
    }

    /**
     * Adds a user
     */
    private void addUser() {
        String firstname, lastname, email, password;
        System.out.print("Write the first name of the user: ");
        firstname = console.nextLine();
        System.out.print("Write the last name of the user: ");
        lastname = console.nextLine();
        System.out.print("Write the email of the user: ");
        email = console.nextLine();
        System.out.print("Write the password of the user: ");
        password = console.nextLine();
        if (!verifyPassword(password)) {
            System.out.println("Password has to contain at least 6 characters");
            return;
        }
        try {
            srv.addUser(firstname, lastname, email, password);
            System.out.println("The user was added");
        } catch (ValidatorException | DbException | RepoException e) {
            System.out.println(e.getMessage());
        }
    }
}
