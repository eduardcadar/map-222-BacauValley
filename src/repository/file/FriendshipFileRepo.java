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
     * @param filename - String numele fisierului cu prietenii
     * @param val - validator de prietenii
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
            throw new FileException("Eroare fisier");
        }
    }

    @Override
    public void removeUserFships(String email) {
        super.removeUserFships(email);
        writeAllToFile(filename);
    }

    /**
     * Incarca in memorie prieteniile dintr-un fisier
     * @param filename - numele fisierului din care se citesc prieteniile
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
            throw new FileException("Eroare fisier");
        } catch (IOException e) {
            throw new FileException("Eroare la citirea din fisier");
        }
    }

    /**
     * Creeaza o prietenie dintr-o lista de String-uri
     * Lista contine:
     *  pe pozitia 0 email-ul primului utilizator
     *  pe pozitia 1 email-ul celui de-al doilea utilizator
     * @param attr - lista cu email-urile celor doi utilizatori
     * @return prietenia creata - Friendship
     */
    private Friendship extractFriendship(List<String> attr) {
        if (attr.size() != 2) throw new FileException("Date eronate in fisierul de prietenii");
        return new Friendship(attr.get(0), attr.get(1));
    }

    /**
     * Creeaza un string cu email-urile utilizatorilor unei prietenii
     * @param f - prietenia din care se creeaza String-ul
     * @return linia creata - String
     */
    private String createLine(Friendship f) {
        return f.getFirst() + "," + f.getSecond();
    }

    /**
     * Scrie toate prieteniile in fisier
     * @param filename - String numele fisierului
     */
    private void writeAllToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Friendship f : super.getAll()) {
                bw.write(createLine(f));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FileException("Eroare la scrierea in fisier");
        }
    }

    /**
     * Salveaza o prietenie in memorie si in fisier
     * @param f - prietenia care va fi adaugata
     * @throws RepoException - daca prietenia este deja salvata
     */
    @Override
    public void addFriendship(Friendship f) throws RepoException {
        super.addFriendship(f);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(createLine(f));
            bw.newLine();
        } catch (IOException e) {
            throw new FileException("Eroare la scrierea in fisier");
        }
    }

    /**
     * Sterge o prietenie din memorie si din fisier
     * @param f - prietenia care va fi stearsa
     * @throws RepoException - daca prietenia nu este salvata
     */
    @Override
    public void removeFriendship(Friendship f) throws RepoException {
        super.removeFriendship(f);
        writeAllToFile(filename);
    }

    /**
     * Sterge toate prieteniile din memorie si din fisier
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
}
