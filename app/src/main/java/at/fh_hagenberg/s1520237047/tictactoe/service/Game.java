package at.fh_hagenberg.s1520237047.tictactoe.service;

import java.io.IOException;

import at.fh_hagenberg.s1520237047.tictactoe.model.Field;
import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.model.MoveNotPossibleException;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.ConnectionLostException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.NoObserverSetException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.NotConnectedException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.OpponentSurrenderedException;

/**
 * @author Kai Takac
 */

public class Game {

    private TicTacToeGameObserver observer;
    private Field field;
    private Player localPlayer;
    private Player remotePlayer;
    private GameConnector connector;
    private boolean myTurn;
    private boolean running = true;

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setObserver(TicTacToeGameObserver observer) {
        this.observer = observer;
    }

    public interface TicTacToeGameObserver {
        void onMyMoveComplete(Game game, Move move);

        void onSurrenderSuccessful(Game game);

        void onOpponentsMoveComplete(Game game, Move move);

        void onOpponentSurrendered(Game game, Player player);

        void onConnectionClosed(Game game);

        void onMatchDraw(Game game);

        void onMatchWin(Game game, Player winner);
    }

    public Game(Field field, Player localPlayer, Player remotePlayer, GameConnector connector, boolean myTurn) {
        this.field = field;
        this.localPlayer = localPlayer;
        this.remotePlayer = remotePlayer;
        this.connector = connector;
        this.myTurn = myTurn;
    }

    public void playMove(int x, int y) throws MoveNotPossibleException, NotConnectedException, NoObserverSetException {
        this.checkMovePreconditions();


        Move move = new Move(this.localPlayer, x, y);
        field.setMove(move);
        this.myTurn = false;

        this.sendMoveAsynchronously(move);
    }

    private void sendMoveAsynchronously(final Move move) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connector.sendMove(move);
                    observer.onMyMoveComplete(Game.this, move);
                    checkForEnd();
                } catch (IOException | ConnectionLostException e) {
                    onConnectionClosed();
                }
            }
        }).start();
    }

    private void checkForEnd() {
        Player winner;
        if(this.field.isFull()) {
            this.myTurn = false;
            this.running = false;
            this.close(false);
            this.observer.onMatchDraw(this);
        } else if ((winner = this.field.getWinner()) != null) {
            this.myTurn = false;
            this.running = false;
            this.close(false);
            this.observer.onMatchWin(this, winner);
        }
    }

    public void waitForOpponentsMove() throws NotConnectedException, MoveNotPossibleException, NoObserverSetException {
        if (connector.isClosed()) {
            throw new NotConnectedException();
        } else if (this.isMyTurn()) {
            throw new MoveNotPossibleException();
        } else if (this.observer == null) {
            throw new NoObserverSetException();
        }

        waitForOpponentsMoveAsynchronously();
    }

    private void waitForOpponentsMoveAsynchronously() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Move move = connector.getOpponentsMove();
                    if (move == null) {
                        throw new RuntimeException("Move may never be null");
                    }
                    move.player = remotePlayer;
                    field.setMove(move);
                    myTurn = true;
                    observer.onOpponentsMoveComplete(Game.this, move);
                    checkForEnd();
                } catch (OpponentSurrenderedException e) {
                    onOpponentSurrendered();
                } catch (IOException | ConnectionLostException e) {
                    onConnectionClosed();
                } catch (MoveNotPossibleException e) {
                    //If the move is not possible something is terribly wrong -> close the connection
                    try {
                        connector.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    onConnectionClosed();
                }
            }
        }).start();
    }

    private void onOpponentSurrendered() {
        this.running = false;
        this.observer.onOpponentSurrendered(this, this.remotePlayer);
    }

    private void onConnectionClosed() {
        this.running = false;
        this.observer.onConnectionClosed(this);
    }

    public void surrender() throws MoveNotPossibleException, NotConnectedException, NoObserverSetException {
        this.checkMovePreconditions();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connector.surrender();
                    running = false;
                    observer.onSurrenderSuccessful(Game.this);
                } catch (IOException | ConnectionLostException e) {
                    onConnectionClosed();
                }
            }
        }).start();
    }

    private void checkMovePreconditions() throws MoveNotPossibleException, NotConnectedException, NoObserverSetException {
        if (connector == null || connector.isClosed()) {
            throw new NotConnectedException();
        } else if (!this.isMyTurn() && running) {
            throw new MoveNotPossibleException();
        } else if (this.observer == null) {
            throw new NoObserverSetException();
        }
    }

    public void close (boolean fireEvent) {
        try {
            if (this.connector != null)
                this.connector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.running = false;

        if(fireEvent){
            onConnectionClosed();
        }
    }

    public void close() {
        close(true);
    }

    public boolean isRunning() {
        return running;
    }

    public Field getField() {
        return field;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public Player getRemotePlayer() {
        return remotePlayer;
    }
}
