package ui;

import Utils.PasswordEncryptor;
import Utils.UserFriendDTO;
import domain.Friendship;
import domain.Message;
import domain.User;
import repository.RepoException;
import repository.db.DbException;
import service.Service;

import java.util.*;

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

        loggedUser = srv.getUser(email);
        if (loggedUser == null)
            return false;
        if(!loggedUser.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(password))))
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
        System.out.println("5. Accept friend request");
        System.out.println("6. Show friends by month");
        System.out.println("7. Send message");
        System.out.println("10. Show conversation with friend");
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
                case "4" -> showFriendsFormatted(loggedUser.getEmail());
                case "5" -> acceptFriendRequest();
                case "6" -> showFriendsByMonth(loggedUser.getEmail());
                case "7" -> sendMessage();
                case "10" -> showConversationWithUser();
                default -> System.out.println("Wrong command");
            }
        }
        System.out.println("Exiting logged interface...");
    }

    public void sendMessage() {
        Map<Integer, String> friendsMap = new HashMap<>();
        List<User> friendsList =  showFriends(loggedUser.getEmail());
        if (friendsList == null) return;
        int i = 0;
        for (User friend : friendsList) {
            i++;
            friendsMap.put(i, friend.getEmail());
        }
        List<String> receivers = new ArrayList<>();
        System.out.print("Write number of friend to add to receivers: ");
        Integer nr = getInteger();
        while (!nr.equals(0)) {
            String friend = friendsMap.get(nr);
            if (!receivers.contains(friend))
                receivers.add(friend);
            else
                System.out.println("Friend already added");
            System.out.print("Write number of friend to add to receivers (or 0 if you added all of them): ");
            nr = getInteger();
        }
        if (receivers.isEmpty()) {
            System.out.println("At least 1 receiver please :(");
            return;
        }
        System.out.print("Write message: ");
        String messageText = console.nextLine();
        srv.save(loggedUser.getEmail(), receivers, messageText);
    }

    public void showConversationWithUser() {
        Map<Integer, String> friendsMap = new HashMap<>();
        List<User> friendsList =  showFriends(loggedUser.getEmail());
        System.out.print("Write the number of the friend: ");
        int i = 0;
        if (friendsList != null) {
            for (User friend : friendsList) {
                i++;
                friendsMap.put(i, friend.getEmail());
            }
            Integer numberOfUser = askForUserNumberInput(friendsMap);
            if (numberOfUser == 0)
                return;
            List<Message> messages = srv.getConversation(loggedUser.getEmail(), friendsMap.get(numberOfUser));
            messages.forEach(x -> System.out.println(x));
        }
    }

    /**
     * Accept friend request menu
     * First there are printed all friend requests for the logged user
     * Second user chooses a number = the friend request that he wants to accept
     */
    private void acceptFriendRequest() {
        Map<Integer, User> usersMap = showFriendRequests();
        if (usersMap.size() == 0)
            return;
        Integer friendRequested = askNumberOfFriendRequests();
        if(friendRequested != null && friendRequested == 0) {
            return;
        }
        try {
            Friendship f = srv.getFriendship(loggedUser.getEmail(), usersMap.get(friendRequested).getEmail());
            // f nu poate fi null (se arunca exceptie daca nu se gaseste prietenia)
            srv.acceptFriendship(f);
            System.out.println("Accepted friend request");
        } catch (NullPointerException e) {
            System.out.println("Invalid number");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void updateUser() {
        System.out.print("Write the new last name: ");
        String lastname = console.nextLine();
        System.out.print("Write the new first name: ");
        String firstname = console.nextLine();
        System.out.print("Write the new password: ");
        String password = console.nextLine();
        try {
            srv.updateUser(firstname, lastname, loggedUser.getEmail(), password);
            loggedUser.update(firstname, lastname);
            System.out.println("Updated");
        } catch (DbException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints all the users that are not friend with user with id = email
     * @param email - String - email of user
     * @return Map<Integer, String> - key = number of user, value = his email
     */
    private Map<Integer, String> printNotFriends(String email){
        List<User> notFriends = srv.getNotFriends(email);
        Map<Integer, String> users = new HashMap<>();
        Integer i = 0;
        for (User u : notFriends) {
            i++;
            users.put(i, u.getEmail());
        }
        System.out.println("----USERS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + srv.getUser(users.get(j)));
        return users;
    }


    private void addFriend() {
        Map<Integer, String> users = printNotFriends(loggedUser.getEmail());
        if (users.size() < 1) {
            System.out.println("No user available for friend request");
            return;
        }
        System.out.print("Write the number of the user: ");
        int userNumber = askForUserNumberInput(users);
        if(userNumber == 0)
            return;
        try {
            srv.addFriendship(loggedUser.getEmail(), users.get(userNumber));
            System.out.println("The friend request was sent");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        }
    }

    private Integer askForUserNumberInput(Map<Integer, String> users) {
        Integer userNumber = getInteger();
        if (userNumber == null) return 0;
        if (userNumber < 1 || userNumber > users.size() + 1) {
            System.out.println("Invalid number");
            return 0;
        }
        return userNumber;
    }

    private Integer getInteger() {
        int userNumber;
        try {
            userNumber = console.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Wrong input");
            return null;
        } finally {
            console.nextLine();
        }
        return userNumber;
    }

    /**
     * Removes a friend from the list of friends for the loggedUser
     */
    private void removeFriend() {
        Map<Integer, String> friendsMap = new HashMap<>();
        List<User> friendsList =  showFriends(loggedUser.getEmail());
        System.out.print("Write the number of the friend you wish to remove: ");
        int i = 0;
        if(friendsList != null) {
            for (User friend : friendsList) {
                i++;
                friendsMap.put(i, friend.getEmail());
            }
            Integer numberOfUser = askForUserNumberInput(friendsMap);
            if (numberOfUser == 0)
                return;
            try {
                srv.removeFriendship(friendsMap.get(numberOfUser), loggedUser.getEmail());
                System.out.println("The friend was removed");
            } catch (RepoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Return and prints a list of users that are friends with user with email
     * @param email String
     * @return
     */
    private List<User> showFriends(String email) {
        List<User> friends = srv.getUserFriends(email);
        if (friends.size() == 0) {
            System.out.println("You don't have any friends :(");
            return null;
        }
        System.out.println("----FRIENDS----");
        int i = 0;
        for (User friend : friends) {
            i++;
            System.out.println(i + ". " + friend);
        }
        return friends;
    }


    /**
     * Prints a list with all approved friendships for the user
     * @param email - String
     */
    private void showFriendsFormatted(String email) {
        List<UserFriendDTO> dtos = srv.getFriendshipsDTO(email);
        if (dtos.size() == 0) {
            System.out.println("You don't have any friends :(");
            return ;
        }
        System.out.println("----FRIENDS----");

        dtos.forEach( System.out::println);

    }

    /**
     * Asks user to input a month to print the friendships from that month
     * @param email
     */
    private void showFriendsByMonth(String email){
        System.out.print("Input the month: ");
        int month;
        try {
            month = console.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Wrong input");
            return;
        } finally {
            console.nextLine();
        }
        printFriendsByMonth(email, month);
    }

    /**
     * Prints the friendships for user with email that started in the specified month
     * @param email
     * @param month
     */
    private void printFriendsByMonth(String email, int month) {
        List<UserFriendDTO> dtos = srv.getFriendshipsDTO(email);
        if (dtos.size() == 0) {
            System.out.println("You don't have any friends :(");
            return ;
        }
        System.out.println("----FRIENDS----");
        // if the stream is empty should print ("You dont have any friends from that month")
        // to be consistent with the :( prints
        dtos.stream()
                .filter(x -> x.getDate().getMonth().getValue() == month)
                .forEach( System.out::println);
    }


    /**
     * Prints all friend requests received by loggedUser
     * Returns a map where key is number of user, value is the user
     * @return Map<Integer, User>
     */
    private Map<Integer, User>  showFriendRequests() {
        List<User> friendRequests = srv.getUserFriendRequests(loggedUser.getEmail());
        Map<Integer, User> usersMap = new HashMap<>();
        if (friendRequests.size() == 0) {
            System.out.println("No friend requests");
            return usersMap;
        }
        Integer i = 0;
        for (User user : friendRequests) {
            i++;
            usersMap.put(i, user);
        }
        System.out.println("----FRIEND REQUESTS----");
        for (Integer j = 1; j <= i; j++)
            System.out.println(j + ". " + usersMap.get(j));
        return usersMap;
    }

    /**
     * Asks user to input a number of a friendship request and returns that number.
     * @return - Integer
     */
    private Integer askNumberOfFriendRequests(){
        System.out.print("Write the number of the request you wish to accept, or 0 to go back: ");
        Integer friendRequested = 0;
        try {
            friendRequested = console.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Wrong input");
        }
        console.nextLine();
        return friendRequested;
    }
}
