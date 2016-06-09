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

    interface TicTacToeGameObserver {
        void onMyMoveComplete(Game game, Move move);

        void onSurrenderSuccessful(Game game);

        void onOpponentsMoveComplete(Game game, Move move);

        void onOpponentSurrendered(Game game, Player player);

        void onConnectionClosed(Game game);
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

        this.myTurn = false;

        Move move = new Move(this.localPlayer, x, y);
        field.setMove(move);

        this.sendMoveAsynchronously(move);
    }

    private void sendMoveAsynchronously(final Move move) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connector.sendMove(move);
                    observer.onMyMoveComplete(Game.this, move);
                    waitForOponentsMove();
                } catch (IOException | ConnectionLostException e) {
                    observer.onConnectionClosed(Game.this);
                }
            }
        }).start();
    }

    private void waitForOponentsMove() {
        if (this.isMyTurn()) {
            return;
        }

        try {
            Move move = connector.getOpponentsMove();
            if(move == null){
                throw new RuntimeException("Move may never be null");
            }
            move.player = remotePlayer;
            this.field.setMove(move);
            this.myTurn = true;
            this.observer.onOpponentsMoveComplete(this, move);
        } catch (OpponentSurrenderedException e) {
            this.onOpponentSurrendered();
        } catch (IOException | ConnectionLostException e) {
            this.onConnectionClosed();
        } catch (MoveNotPossibleException e) {
            //If the move is not possible something is terribly wrong -> close the connection
            try {
                this.connector.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.onConnectionClosed();
        }
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
        if (connector.isClosed()) {
            throw new NotConnectedException();
        } else if (!this.isMyTurn() && running) {
            throw new MoveNotPossibleException();
        } else if (this.observer == null) {
            throw new NoObserverSetException();
        }
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
