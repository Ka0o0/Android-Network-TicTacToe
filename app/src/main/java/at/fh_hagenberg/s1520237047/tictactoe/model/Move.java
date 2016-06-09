package at.fh_hagenberg.s1520237047.tictactoe.model;

/**
 * @author Kai Takac
 */

public class Move {

    public Player player;
    public int x;
    public int y;

    public Move() {
    }

    public Move(Player player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }
}
