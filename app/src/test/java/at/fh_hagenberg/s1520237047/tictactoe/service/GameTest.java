package at.fh_hagenberg.s1520237047.tictactoe.service;

import net.jodah.concurrentunit.Waiter;

import org.junit.Test;

import java.util.concurrent.TimeoutException;

import at.fh_hagenberg.s1520237047.tictactoe.mock.GameConnectorMock;
import at.fh_hagenberg.s1520237047.tictactoe.model.Field;
import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.model.MoveNotPossibleException;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.NoObserverSetException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.NotConnectedException;

import static org.junit.Assert.*;

/**
 * @author Kai Takac
 */
public class GameTest {

    private Player localPlayer = new Player(0, "Ka0o0");
    private Player remotePlayer = new Player(0, "BadA**96");
    private GameConnectorMock connectorMock = new GameConnectorMock();

    private Game createMockedGame(boolean myMove) {
        Game temp = new Game(new Field(3), localPlayer, remotePlayer, connectorMock, myMove);
        temp.setObserver(new Game.TicTacToeGameObserver() {
            @Override
            public void onMyMoveComplete(Game game, Move move) {

            }

            @Override
            public void onSurrenderSuccessful(Game game) {

            }

            @Override
            public void onOpponentsMoveComplete(Game game, Move move) {

            }

            @Override
            public void onOpponentSurrendered(Game game, Player player) {

            }

            @Override
            public void onConnectionClosed(Game game) {

            }
        });

        return temp;
    }

    @Test
    public void isMyTurn() throws NotConnectedException, MoveNotPossibleException {
        Game game = this.createMockedGame(true);
        assertTrue(game.isMyTurn());
        game = this.createMockedGame(false);
        assertFalse(game.isMyTurn());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void playInvalidMove() throws NotConnectedException, MoveNotPossibleException, NoObserverSetException {
        Game game = this.createMockedGame(true);

        game.playMove(10, 10);
    }

    @Test(expected = MoveNotPossibleException.class)
    public void playDoubleMove() throws NotConnectedException, MoveNotPossibleException, NoObserverSetException {
        Game game = this.createMockedGame(true);

        game.playMove(0, 0);
        game.playMove(0, 0);
    }


    @Test
    public void playMove() throws NotConnectedException, MoveNotPossibleException, TimeoutException, NoObserverSetException {
        Game game = this.createMockedGame(true);
        final Move opponentsMove = new Move(remotePlayer, 1, 1);
        connectorMock.setNextOpponentsMove(opponentsMove);
        final Move expectedMove = new Move(localPlayer, 0, 0);
        final Waiter waiter = new Waiter();
        final boolean[] wasCalled = {false, false};

        game.setObserver(new Game.TicTacToeGameObserver() {
            @Override
            public void onMyMoveComplete(Game game, Move move) {
                wasCalled[0] = true;
                assertEquals(expectedMove.x, move.x);
                assertEquals(expectedMove.y, move.y);
                assertEquals(localPlayer.id, move.player.id);
            }

            @Override
            public void onSurrenderSuccessful(Game game) {

            }

            @Override
            public void onOpponentsMoveComplete(Game game, Move move) {
                wasCalled[1] = true;
                assertEquals(opponentsMove.x, move.x);
                assertEquals(opponentsMove.y, move.y);
                assertEquals(remotePlayer.id, move.player.id);

                waiter.resume();
            }

            @Override
            public void onOpponentSurrendered(Game game, Player player) {

            }

            @Override
            public void onConnectionClosed(Game game) {

            }
        });
        game.playMove(expectedMove.x, expectedMove.y);

        assertFalse(game.isMyTurn());
        waiter.await(10000000);
        assertTrue(game.isMyTurn());

        Move move = connectorMock.getLastMove();
        assertNotNull(move);
        assertEquals(expectedMove.x, move.x);
        assertEquals(expectedMove.y, move.y);
        assertEquals(localPlayer.id, move.player.id);
        assertTrue(wasCalled[0]);
        assertTrue(wasCalled[1]);
    }

    @Test
    public void surrender() throws NotConnectedException, MoveNotPossibleException, TimeoutException, NoObserverSetException {
        Game game = this.createMockedGame(true);
        final boolean[] wasCalled = new boolean[]{false};
        final Waiter waiter = new Waiter();

        game.setObserver(new Game.TicTacToeGameObserver() {
            @Override
            public void onMyMoveComplete(Game game, Move move) {

            }

            @Override
            public void onSurrenderSuccessful(Game game) {
                wasCalled[0] = true;
                waiter.resume();
            }

            @Override
            public void onOpponentsMoveComplete(Game game, Move move) {

            }

            @Override
            public void onOpponentSurrendered(Game game, Player player) {

            }

            @Override
            public void onConnectionClosed(Game game) {

            }
        });

        game.surrender();
        waiter.await(1000);
        assertTrue(connectorMock.isSurrendered());
        assertFalse(game.isRunning());
        assertTrue(wasCalled[0]);
    }

    @Test(expected = NoObserverSetException.class)
    public void testEmptyObserver() throws NotConnectedException, MoveNotPossibleException, NoObserverSetException {
        Game game = this.createMockedGame(true);

        game.setObserver(null);
        game.surrender();
    }

}