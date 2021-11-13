package ui;

import domain.Friendship;
import domain.User;
import domain.network.Network;
import repository.FriendshipRepository;
import repository.UserRepository;
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

import java.util.Scanner;

public class MainInterface implements UserInterface {
    private final Scanner console;
    private final LoggedInterface loggedInterface;
    private final AdminInterface adminInterface;
    private final Service srv;

    /**
     * File repo
     * @param usersFile - String the name of the users file
     * @param friendshipsFile - String the name of the friendships file
     */
    public MainInterface(String usersFile, String friendshipsFile) {
        console = new Scanner(System.in);
        Validator<User> uVal = new UserValidator();
        UserRepository uRepo = new UserFileRepo(usersFile, uVal);
        UserService uSrv = new UserService(uRepo);
        Validator<Friendship> fVal = new FriendshipValidator();
        FriendshipRepository fRepo = new FriendshipFileRepo(friendshipsFile, fVal, uRepo);
        FriendshipService fSrv = new FriendshipService(fRepo);
        Network network = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, network);
        this.loggedInterface = new LoggedInterface(console, srv);
        this.adminInterface = new AdminInterface(console, srv);
    }

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
        FriendshipService fSrv = new FriendshipService(fRepo);
        Network network = new Network(uRepo, fRepo);
        srv = new Service(uSrv, fSrv, network);
        this.loggedInterface = new LoggedInterface(console, srv);
        this.adminInterface = new AdminInterface(console, srv);
    }

    @Override
    public void run() {
        System.out.println("1. Log in as user");
        System.out.println("2. Admin");
        System.out.println("0. Exit");

        String com = console.nextLine().strip();
        while (true) {
            switch (com) {
                case "1":
                    loggedInterface.run();
                case "2":
                    adminInterface.run();
                case "0":
                    return;
                default:
                    System.out.println("Wrong command");
            }
        }
    }
}
