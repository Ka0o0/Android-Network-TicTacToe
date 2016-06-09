package at.fh_hagenberg.s1520237047.tictactoe.mock;

import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.service.GameConnector;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.ConnectionLostException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.OpponentSurrenderedException;

/**
 * @author Kai Takac
 */

public class GameConnectorMock extends GameConnector {

    private Move nextOpponentsMove = new Move(null, 1, 1);
    private Move lastMove;
    private boolean surrendered = false;

    public GameConnectorMock() {
        this.closed = false;
    }

    @Override
    public void surrender() throws ConnectionLostException {
        surrendered = true;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move getOpponentsMove() throws OpponentSurrenderedException, ConnectionLostException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.nextOpponentsMove;
    }

    @Override
    public void sendMove(Move move) throws ConnectionLostException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.lastMove = move;
    }

    @Override
    public void close() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setNextOpponentsMove(Move nextOpponentsMove) {
        this.nextOpponentsMove = nextOpponentsMove;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public boolean isSurrendered() {
        return surrendered;
    }

    public void setConnected(boolean connected) {
        this.closed = connected;
    }
}
