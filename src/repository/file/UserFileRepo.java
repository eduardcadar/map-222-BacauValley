package repository.file;

import domain.User;
import repository.UserRepository;
import repository.memory.UserRepoInMemory;
import validator.Validator;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class UserFileRepo extends UserRepoInMemory implements UserRepository {
    private String filename;

    /**
     * @param filename - String the name of the users file
     * @param val - users validator
     */
    public UserFileRepo(String filename, Validator<User> val) {
        super(val);
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

    /**
     * Loads into the memory the users of a file
     * @param filename - the name of the users file
     */
    private void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                if (linie.equals("")) continue;
                List<String> attributes = Arrays.asList(linie.split(","));
                super.save(extractUser(attributes));
            }
        } catch (FileNotFoundException e) {
            throw new FileException("File error");
        } catch (IOException e) {
            throw new FileException("Error on reading from file");
        }
    }

    /**
     * Creates a user from a list of strings
     * The list contains:
     *  list[0] - the first name
     *  list[1] - the last name
     *  list[2] - the email
     * @param attr - the list with the user's attributes
     * @return the user - User
     */
    private User extractUser(List<String> attr) {
        if (attr.size() != 3) throw new FileException("Wrong data as parameter");
        return new User(attr.get(2), attr.get(1), attr.get(0));
    }

    /**
     * Creates a string with the attributes of a user
     * @param u - the user
     * @return the created line - String
     */
    private String createLine(User u) {
        return u.getEmail() + "," + u.getFirstName() + "," + u.getLastName();
    }

    /**
     * Writes to file all the users saved in memory
     * @param filename - the name of the file
     */
    private void writeAllToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User u : super.getAll()) {
                bw.write(createLine(u));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FileException("Error on writing to file");
        }
    }

    /**
     * Adds a user in memory and in file
     * @param u - the user that will be added
     * @throws FileException - error on writing to file
     */
    @Override
    public void save(User u) throws FileException {
        super.save(u);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(createLine(u));
            bw.newLine();
        } catch (IOException e) {
            throw new FileException("Error on writing to file");
        }
    }

    /**
     * Removes a user from memory and from file
     * @param email - the email of the user
     */
    @Override
    public void remove(String email) {
        super.remove(email);
        writeAllToFile(filename);
    }

    /**
     * Removes all the users from memory and from file
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

    @Override
    public void update(User u) {
        super.update(u);
        writeAllToFile(filename);
    }
}
