package ui;

import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.db.*;
import service.*;
import validator.*;

import java.util.Scanner;

public class MainInterface implements UserInterface {
    private final Scanner console;
    private final LoggedInterface loggedInterface;
    private final AdminInterface adminInterface;
    private final Service srv;

    /**
     * Database repo
     * @param url - String the url of the database
     */
    public MainInterface(String url) {
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
        FriendshipRequestDbRepo friendshipRequestRepo = new FriendshipRequestDbRepo(url, username, password,"requests");
        FriendshipService fSrv = new FriendshipService(fRepo, friendshipRequestRepo);
        MessageDbRepo mRepo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
        MessageService mSrv = new MessageService(mRepo);
        MessageReceiverDbRepo mrRepo = new MessageReceiverDbRepo(url, username, password, new MessageReceiverValidator(), "receivers");
        MessageReceiverService mrSrv = new MessageReceiverService(mrRepo);
        Network network = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, mSrv, mrSrv, network);
        this.loggedInterface = new LoggedInterface(console, srv);
        this.adminInterface = new AdminInterface(console, srv);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("1. Log in as user");
            System.out.println("2. Admin");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            String com = console.nextLine().strip();
            if (com.compareTo("0") == 0)
                break;
            switch (com) {
                case "1" -> loggedInterface.run();
                case "2" -> adminInterface.run();
                default -> System.out.println("Wrong command");
            }
        }
    }
}
