package repository.file;

import domain.Friendship;
import repository.FriendshipRepository;
import repository.RepoException;
import repository.UserRepository;
import repository.memory.FriendshipRepoInMemory;
import validator.Validator;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FriendshipFileRepo extends FriendshipRepoInMemory implements FriendshipRepository {
    private String filename;

    /**
     *
     * @param filename - String the name of the friendships file
     * @param val - friendship validator
     */
    public FriendshipFileRepo(String filename, Validator<Friendship> val, UserRepository userRepo) {
        super(val, userRepo);
        this.filename = filename;
        try {
            File f = new File(filename);
            if (!f.exists())
                f.createNewFile();
            loadFromFile(filename);
        } catch (IOException e) {
            throw new FileException("File error");
        }
    }

    @Override
    public void removeUserFships(String email) {
        super.removeUserFships(email);
        writeAllToFile(filename);
    }

    @Override
    public void acceptFriendship(Friendship f) {

    }

    /**
     * Loads into the memory the friendships from a file
     * @param filename - the name of the friendships file
     */
    private void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                if (linie.equals("")) continue;
                List<String> attributes = Arrays.asList(linie.split(","));
                super.addFriendship(extractFriendship(attributes));
            }
        } catch (FileNotFoundException e) {
            throw new FileException("File error");
        } catch (IOException e) {
            throw new FileException("Error on reading from file");
        }
    }

    /**
     * Creates a friendship from a list of strings
     * The list contains:
     *  list[0] - the email of the first user
     *  list[1] - the email of the second user
     * @param attr - the list with the emails of the two users
     * @return the friendship
     */
    private Friendship extractFriendship(List<String> attr) {
        if (attr.size() != 2) throw new FileException("Wrong data as parameter");
        return new Friendship(attr.get(0), attr.get(1));
    }

    /**
     * Creates a string with the emails of a friendship's users
     * @param f - the friendship from which the string is created
     * @return the created line - String
     */
    private String createLine(Friendship f) {
        return f.getFirst() + "," + f.getSecond();
    }

    /**
     * Writes all the friendships in the file
     * @param filename - String the name of the file
     */
    private void writeAllToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Friendship f : super.getAllApproved()) {
                bw.write(createLine(f));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FileException("Error on writing to file");
        }
    }

    /**
     * Saves a friendship in memory and in the file
     * @param f - the friendship that will be added
     * @throws RepoException - if the friendship is already saved
     */
    @Override
    public void addFriendship(Friendship f) throws RepoException {
        super.addFriendship(f);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(createLine(f));
            bw.newLine();
        } catch (IOException e) {
            throw new FileException("Error on writing to file");
        }
    }

    /**
     * Removes a friendship from memory and from the file
     * @param f - the friendship that will be removed
     * @throws RepoException - if the friendship is not saved
     */
    @Override
    public void removeFriendship(Friendship f) throws RepoException {
        super.removeFriendship(f);
        writeAllToFile(filename);
    }

    /**
     * Removes all the friendships from memory and from file
     */
    @Override
    public void clear() {
        try {
            new FileWriter(filename).close();
        } catch (IOException e) {
            throw new FileException("File error");
        }
        super.clear();
    }
}
