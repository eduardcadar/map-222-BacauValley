package ui;

import domain.Friendship;
import domain.User;
import repository.RepoException;
import repository.db.DbException;
import service.Service;
import validator.ValidatorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
