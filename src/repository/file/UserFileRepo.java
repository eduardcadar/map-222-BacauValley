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
     * @param filename - String numele fisierului cu utilizatori
     * @param val - validator de utilizatori
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
            throw new FileException("Eroare fisier");
        }

    }

    /**
     * Incarca in memorie utilizatorii dintr-un fisier
     * @param filename - numele fisierului din care se citesc utilizatorii
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
            throw new FileException("Eroare fisier");
        } catch (IOException e) {
            throw new FileException("Eroare la citirea din fisier");
        }
    }

    /**
     * Creeaza un utilizatori dintr-o lista de String-uri
     * Lista contine:
     *  pe pozitia 0 prenume
     *  pe pozitia 1 nume de familie
     *  pe pozitia 2 email
     * @param attr - lista cu atributele utilizatorului
     * @return utilizatorul creat - User
     */
    private User extractUser(List<String> attr) {
        if (attr.size() != 3) throw new FileException("Date eronate in fisierul de utilizatori");
        return new User(attr.get(2), attr.get(1), attr.get(0));
    }

    /**
     * Creeaza un string cu datele unui utilizator
     * @param u - utilizatorul
     * @return linia creata - String
     */
    private String createLine(User u) {
        return u.getEmail() + "," + u.getFirstName() + "," + u.getLastName();
    }

    /**
     * Scrie in fisier toti userii salvati in memorie
     * @param filename - numele fisierului in care se scrie
     * @throws IOException - eroare la scrierea in fisier
     */
    private void writeAllToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User u : super.getAll()) {
                bw.write(createLine(u));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FileException("Eroare la scrierea in fisier");
        }
    }

    /**
     * Adauga un utilizator in memorie si in fisier
     * @param u - User-ul care va fi adaugat
     * @throws FileException - eroare la scrierea in fisier
     */
    @Override
    public void save(User u) throws FileException {
        super.save(u);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(createLine(u));
            bw.newLine();
        } catch (IOException e) {
            throw new FileException("Eroare la scrierea in fisier");
        }
    }

    /**
     * Sterge un utilizator din memorie si din fisier
     * @param email - email-ul utilizatorului care se va sterge
     * @throws FileException - eroare la deschiderea fisierului
     */
    @Override
    public void remove(String email) {
        super.remove(email);
        writeAllToFile(filename);
    }

    /**
     * Sterge toti utilizatorii din memorie si din fisier
     * @throws FileException - eroare la deschiderea fisierului
     */
    @Override
    public void clear() {
        try {
            new FileWriter(filename).close();
        } catch (IOException e) {
            throw new FileException("Eroare fisier");
        }
        super.clear();
    }

    @Override
    public void update(User u) {
        super.update(u);
        writeAllToFile(filename);
    }
}
