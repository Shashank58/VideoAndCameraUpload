package shashank.treusbs;

/**
 * Created by shashankm on 30/04/16.
 */
public class User {
    String name;
    String number;

    public User(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public User() {
        //Empty constructor for fire base
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
