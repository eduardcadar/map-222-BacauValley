import repository.db.DbException;
import ui.MainInterface;

public class Main {

    public static void main(String[] args) {
        //DATABASE
        try {
            MainInterface ui = new MainInterface("jdbc:postgresql://localhost:5432/ToySocialNetwork");
            ui.run();
        } catch (DbException e) {
            System.out.println(e.getMessage());
        }
    }
}

// jdbc:postgresql://localhost:5432/ToySocialNetwork
