package at.fh_hagenberg.s1520237047.tictactoe.service;

/**
 * @author Kai Takac
 */

public class GlobalGame {

    private static GlobalGame instance;
    private Game game;

    public static GlobalGame getInstance() {
        if (instance == null) {
            instance = new GlobalGame();
        }
        return instance;
    }


    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
