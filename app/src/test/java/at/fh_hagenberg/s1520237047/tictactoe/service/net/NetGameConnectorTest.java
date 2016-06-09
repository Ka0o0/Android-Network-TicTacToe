package at.fh_hagenberg.s1520237047.tictactoe.service.net;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.ConnectionLostException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Kai Takac
 */
public class NetGameConnectorTest {

    Socket socket;
    ByteArrayOutputStream byteArrayOutputStream;
    ByteArrayInputStream byteArrayInputStream;

    @Before
    public void beforeTest() throws IOException {
        socket = mock(Socket.class);
        byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayInputStream = new ByteArrayInputStream("move\n10;2".getBytes());
        when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);
        when(socket.getInputStream()).thenReturn(byteArrayInputStream);

    }

    @Test
    public void surrender() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        gameConnector.surrender();
        assertEquals("surrender\n", byteArrayOutputStream.toString());
        assertTrue(gameConnector.isClosed());
    }

    @Test(expected = IOException.class)
    public void surrenderClosed() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        gameConnector.close();
        gameConnector.surrender();
    }

    @Test
    public void getOpponentsMove() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        Move move = gameConnector.getOpponentsMove();
        assertEquals(10, move.x);
        assertEquals(2, move.y);
    }

    @Test(expected = IOException.class)
    public void getOpponentsMoveClosed() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        gameConnector.close();
        gameConnector.getOpponentsMove();
    }

    @Test
    public void sendMove() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        Move move = new Move(new Player(0, "Test"), 2, 10);
        gameConnector.sendMove(move);
        assertEquals("move\n2;10\n", byteArrayOutputStream.toString());
    }

    @Test(expected = IOException.class)
    public void sendMoveClosed() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        Move move = new Move(new Player(0, "Test"), 2, 10);
        gameConnector.close();
        gameConnector.sendMove(move);
    }

    @Test
    public void close() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        gameConnector.close();
        assertTrue(gameConnector.isClosed());
    }

    @Test(expected = IOException.class)
    public void closeClosed() throws Exception {
        NetGameConnector gameConnector = new NetGameConnector(socket);
        gameConnector.close();
        gameConnector.close();
    }
}