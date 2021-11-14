import repository.db.DbException;
import repository.file.FileException;
import ui.Interface;
import validator.ValidatorException;

public class Main {

    public static void main(String[] args) {
        //FILE
//        try {
//            ui.Interface ui = new ui.Interface("users.csv", "friendships.csv");
//            ui.run();
//        } catch (FileException e) {
//            System.out.println(e.getMessage());
//        } catch (ValidatorException e) {
//            System.out.print("Wrong data in file: ");
//            System.out.println(e.getMessage());
//        }

        //DATABASE
        try {
            Interface ui = new Interface("jdbc:postgresql://localhost:5432/ToySocialNetwork");
            ui.run();
        } catch (DbException e) {
            System.out.println(e.getMessage());
        }
    }
}

// jdbc:postgresql://localhost:5432/ToySocialNetwork
