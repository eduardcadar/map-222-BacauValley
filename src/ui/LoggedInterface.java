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

    /**
     * Login the user
     * @return true if the login was successful, false otherwise
     */
    public boolean login() {
        System.out.print("Email: ");
        String email = console.nextLine().strip();
        System.out.print("Password: ");
        String password = console.nextLine().strip();

        loggedUser = srv.getUser(email);
        if (loggedUser == null)
            return false;
        return loggedUser.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(password)));
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

    /**
     * Sends a message to other users
     */
    public void sendMessage() {
        printFriends(loggedUser.getEmail());
        Map<Integer, User> friendsMap = getFriendsMap(loggedUser.getEmail());
        if (friendsMap.size() == 0) return;

        List<String> receivers = new ArrayList<>();
        System.out.print("Write number of friend to add to receivers: ");
        int nr = askForUserNumberInput(friendsMap.size());
        while (nr != 0) {
            User friend = friendsMap.get(nr);
            if (!receivers.contains(friend.getEmail()))
                receivers.add(friend.getEmail());
            else
                System.out.println("Friend already added");
            System.out.print("Write number of friend to add to receivers (or 0 if you added all of them): ");
            nr = askForUserNumberInput(friendsMap.size());
        }
        if (receivers.isEmpty()) {
            System.out.println("At least 1 receiver please :(");
            return;
        }
        System.out.print("Write message: ");
        String messageText = console.nextLine();
        srv.save(loggedUser.getEmail(), receivers, messageText);
    }

    /**
     * Shows conversation between logged user and another user
     */
    public void showConversationWithUser() {
        printFriends(loggedUser.getEmail());
        Map<Integer, User> friendsMap = getFriendsMap(loggedUser.getEmail());
        if (friendsMap.size() == 0)
            return;
        System.out.print("Write the number of the friend: ");
        int numberOfUser = askForUserNumberInput(friendsMap.size());
        if (numberOfUser == 0)
            return;
        List<Message> messages = srv.getConversation(loggedUser.getEmail(), friendsMap.get(numberOfUser).getEmail());
        messages.forEach(System.out::println);
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

    /**
     * Updates the logged user
     */
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
     * Returns a map with the users that are not friends with a user
     * @param email email of user
     * @return Map<Integer, User> - key = number of user, value = the user
     */
    private Map<Integer, User> getNotFriendsMap(String email){
        List<User> notFriends = srv.getNotFriends(email);
        Map<Integer, User> notFriendsMap = new HashMap<>();
        int i = 0;
        for (User user : notFriends) {
            i++;
            notFriendsMap.put(i, user);
        }
        return notFriendsMap;
    }

    /**
     * Prints the users that are not friends with a user
     * @param email email of user
     */
    private void printNotFriends(String email) {
        List<User> notFriends = srv.getNotFriends(email);
        int i = 0;
        for (User user : notFriends) {
            i++;
            System.out.println(i + ". " + user);
        }
    }

    /**
     * Adds a friend to the logged user
     */
    private void addFriend() {
        printNotFriends(loggedUser.getEmail());
        Map<Integer, User> notFriendsMap = getNotFriendsMap(loggedUser.getEmail());
        if (notFriendsMap.size() == 0) {
            System.out.println("No user available to friend request");
            return;
        }
        System.out.print("Write the number of the user: ");
        int userNumber = askForUserNumberInput(notFriendsMap.size());
        if(userNumber == 0)
            return;
        try {
            srv.addFriendship(loggedUser.getEmail(), notFriendsMap.get(userNumber).getEmail());
            System.out.println("The friend request was sent");
        } catch (RepoException | DbException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns a number read from keyboard
     * @param nrUsers max nr to be read from keyboard
     * @return int number of user read from keyboard
     */
    private Integer askForUserNumberInput(int nrUsers) {
        Integer userNumber = getInteger();
        if (userNumber == null) return 0;
        if (userNumber < 0 || userNumber > nrUsers) {
            System.out.println("Invalid number");
            return 0;
        }
        return userNumber;
    }

    /**
     * Reads an int from console
     * @return integer input
     */
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
        printFriends(loggedUser.getEmail());
        Map<Integer, User> friendsMap = getFriendsMap(loggedUser.getEmail());
        if (friendsMap.size() == 0)
            return;
        System.out.print("Write the number of the friend you wish to remove: ");
        int i = 0;
        int numberOfUser = askForUserNumberInput(friendsMap.size());
        if (numberOfUser == 0)
            return;
        try {
            srv.removeFriendship(friendsMap.get(numberOfUser).getEmail(), loggedUser.getEmail());
            System.out.println("The friend was removed");
        } catch (RepoException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints a list of users that are friends with user with email
     * @param email user email
     */
    private void printFriends(String email) {
        List<User> friends = srv.getUserFriends(email);
        if (friends.size() == 0) {
            System.out.println("You don't have any friends :(");
            return;
        }
        System.out.println("----FRIENDS----");
        int i = 0;
        for (User friend : friends) {
            i++;
            System.out.println(i + ". " + friend);
        }
    }

    /**
     * Returns a map with a user's friends
     * @param email user email
     * @return map with key number of user, value the user
     */
    private Map<Integer, User> getFriendsMap(String email) {
        List<User> friends = srv.getUserFriends(email);
        Map<Integer, User> friendsMap = new HashMap<>();
        int i = 0;
        for (User user : friends) {
            i++;
            friendsMap.put(i, user);
        }
        return friendsMap;
    }


    /**
     * Prints a list with all approved friendships for the user
     * @param email user email
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
     * @param email user email
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
