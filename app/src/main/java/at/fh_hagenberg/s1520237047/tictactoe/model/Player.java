package at.fh_hagenberg.s1520237047.tictactoe.model;

/**
 * @author Kai Takac
 */

public class Player {

    public int id;
    public String name;

    public Player() {
    }

    public Player(int id, String name) {
        if (id < 0) {
            throw new IllegalArgumentException("id must not be smaller than 0");
        } else if (name.trim().equals("")) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        this.id = id;
        this.name = name;
    }
}
