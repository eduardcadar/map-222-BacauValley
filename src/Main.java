import repository.db.DbException;
import repository.file.FileException;
import validator.ValidatorException;

public class Main {

    public static void main(String[] args) {
        //FILE
//        try {
//            Interface ui = new Interface("users.csv", "friendships.csv");
//            ui.run();
//        } catch (FileException e) {
//            System.out.println(e.getMessage());
//        } catch (ValidatorException e) {
//            System.out.print("Wrong data in file: ");
//            System.out.println(e.getMessage());
//        }
        System.out.println("BIG BOSS BACK IN PROJECT");
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
