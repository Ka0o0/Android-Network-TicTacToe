package at.fh_hagenberg.s1520237047.tictactoe.service;

/**
 * @author Kai Takac
 */

public interface GameCreator {

    interface GameCreatorHandler {
        void onGameCreated(Game game);
        void onGameCreationFailed();
    }

    void createGame(int fieldSize, GameCreatorHandler handler);

}
