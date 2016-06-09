package at.fh_hagenberg.s1520237047.tictactoe.service.net;

import net.jodah.concurrentunit.Waiter;

import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;

import at.fh_hagenberg.s1520237047.tictactoe.service.Game;
import at.fh_hagenberg.s1520237047.tictactoe.service.GameCreator;

import static org.junit.Assert.*;

/**
 * @author Kai Takac
 */
public class NetServerGameCreatorTest {

    @Test
    public void createGame() throws Exception {
        final Waiter waiter = new Waiter();
        final Waiter waiter2 = new Waiter();

        final String localPlayerName = "Ka0o0";
        final String remotePlayerName = "BadA**96";
        final int gameSize = 0;
        final boolean[] called = new boolean[]{false, false};

        NetServerGameCreator gameCreator = new NetServerGameCreator(localPlayerName);
        gameCreator.createGame(gameSize, new GameCreator.GameCreatorHandler() {
            @Override
            public void onGameCreated(Game game) {
                assertTrue(game.isMyTurn());
                assertEquals(gameSize, game.getField().getSize());
                assertEquals(remotePlayerName, game.getRemotePlayer().name);
                assertEquals(localPlayerName, game.getLocalPlayer().name);
                called[0] = true;
                waiter.resume();
            }

            @Override
            public void onGameCreationFailed() {
            }
        });

        NetClientGameCreator clientGameCreator = new NetClientGameCreator(remotePlayerName, InetAddress.getByName("localhost"));
        clientGameCreator.createGame(0, new GameCreator.GameCreatorHandler() {
            @Override
            public void onGameCreated(Game game) {
                assertFalse(game.isMyTurn());
                assertEquals(gameSize, game.getField().getSize());
                assertEquals(localPlayerName, game.getRemotePlayer().name);
                assertEquals(remotePlayerName, game.getLocalPlayer().name);
                called[1] = true;
                waiter2.resume();
            }

            @Override
            public void onGameCreationFailed() {

            }
        });

        waiter.await(2000);
        assertTrue(called[0]);
        waiter2.await(2000);
        assertTrue(called[1]);

    }

}