package ui;

import domain.Friendship;
import domain.User;
import repository.RepoException;
import repository.db.DbException;
import service.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LoggedInterface implements UserInterface {
    private final Scanner console;
    private final Service srv;
    private User loggedUser;

    public LoggedInterface(Scanner console, Service srv) {
        this.console = console;
        this.srv = srv;
    }

    public boolean login() {
        System.out.print("Email: ");
        String email = console.nextLine().strip();
        System.out.print("Password: ");
        String password = console.nextLine().strip();

        // TODO - verify login

        if ((loggedUser = srv.getUser(email)) == null)
            return false;
        return true;
    }

    private String menu() {
        System.out.println();
        System.out.println("LOGGED USER: " + loggedUser);
        System.out.println("1. Update user");
        System.out.println("2. Add friend");
        System.out.println("3. Remove friend");
        System.out.println("4. Show friends");
        System.out.println("5. Show friend requests");
        System.out.println("0. Exit");
        System.out.print("Write command: ");
        return console.nextLine().strip();
    }

    @Override
    public void run() {
        if (!login()){
            System.out.println("Wrong email or password");
            return;
        }

        while (true) {
            String command = menu();
            if (command.compareTo("0") == 0) break;
            switch (command) {
                case "1" -> updateUser();
                case "2" -> addFriend();
                case "3" -> removeFriend();
                case "4" -> showFriends();
                case "5" -> showFriendRequests();
                default -> System.out.println("Wrong command");
            }
        }
        System.out.println("Exiting logged interface...");
    }

    private void updateUser() {
        System.out.print("Write the new last name: ");
        String lastname = console.nextLine();
        System.out.print("Write the new first name: ");
        String firstname = console.nextLine();
        try {
            srv.updateUser(new User(firstname, lastname, loggedUser.getEmail()));
            loggedUser = new User(firstname, lastname, loggedUser.getEmail());
            System.out.println("Updated");
        } catch (DbException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addFriend() {
        if (srv.usersSize() < 1) {
            System.out.println("Not enough users saved");
            return;
        }
        List<User> notFriends = srv.getNotFriends(loggedUser.getEmail());
        Map<Integer, String> users = new HashMap<>();
        Integer i = 0;
        for (User u : notFriends) {
            i++;
            users.put(i, u.getEmail());
        }
        System.out.println("----USERS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + srv.getUser(users.get(j)));
        System.out.print("Write the number of the user: ");
        int a = console.nextInt();
        if (a < 1 || a > i) {
            System.out.println("Invalid number");
            return;
        }
        console.nextLine();
        try {
            Friendship f = new Friendship(loggedUser.getEmail(), users.get(a));
            srv.addFriendship(f);
            System.out.println("The friend request was sent");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeFriend() {
        List<User> friends = srv.getUserFriends(loggedUser.getEmail());
        if (friends.size() == 0) {
            System.out.println("You don't have any friends :(");
            return;
        }
        Map<Integer, User> friendsMap = new HashMap<>();
        Integer i = 0;
        for (User friend : friends) {
            i++;
            friendsMap.put(i, friend);
        }
        System.out.println("----FRIENDS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + friendsMap.get(j));
        System.out.print("Write the number of the friend you wish to remove: ");
        Integer a = console.nextInt();
        console.nextLine();
        try {
            srv.removeFriendship(new Friendship(friendsMap.get(a).getEmail(), loggedUser.getEmail()));
            System.out.println("The friend was removed");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Invalid number");
        }
    }

    private void showFriends() {
        List<User> friends = srv.getUserFriends(loggedUser.getEmail());
        System.out.println("----FRIENDS----");
        int i = 0;
        for (User friend : friends) {
            i++;
            System.out.println(i + ". " + friend);
        }
    }

    private void showFriendRequests() {
//        List<User> friendRequests = srv.getUserFriendRequests(loggedUser.getEmail());
//        Map<Integer, User> usersMap = new HashMap<>();
//        Integer i = 0;
//        for (User user : friendRequests) {
//            i++;
//            usersMap.put(i, user);
//        }
//        System.out.println("----FRIEND REQUESTS----");
//        for (Integer j = 1; j <= i; j++)
//            System.out.println(j + ". " + usersMap.get(j));
//        System.out.println("Write the number of the request you wish to accept, or 0 to go back: ");
//        Integer a = console.nextInt();
//        console.nextLine();
//        try {
//
//        } catch (NullPointerException e) {
//            System.out.println("Invalid number");
//        }
    }
}
