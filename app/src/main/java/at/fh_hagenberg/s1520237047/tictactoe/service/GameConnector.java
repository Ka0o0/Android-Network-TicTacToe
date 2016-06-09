package at.fh_hagenberg.s1520237047.tictactoe.service;

import java.io.IOException;

import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.ConnectionLostException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.OpponentSurrenderedException;

/**
 * @author Kai Takac
 */

public abstract class GameConnector {


    protected boolean closed;

    public boolean isClosed() {
        return closed;
    }

    public abstract void surrender() throws ConnectionLostException, IOException;

    public abstract Move getOpponentsMove() throws OpponentSurrenderedException, ConnectionLostException, IOException;

    public abstract void sendMove(Move move) throws ConnectionLostException, IOException;

    public abstract void close() throws IOException;
}
