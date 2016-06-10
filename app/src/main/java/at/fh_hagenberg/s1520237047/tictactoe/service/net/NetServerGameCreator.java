package at.fh_hagenberg.s1520237047.tictactoe.service.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import at.fh_hagenberg.s1520237047.tictactoe.model.Field;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.Game;
import at.fh_hagenberg.s1520237047.tictactoe.service.GameCreator;

/**
 * @author Kai Takac
 */

public class NetServerGameCreator implements GameCreator {

    public static final int SERVER_PORT = 63340;

    private Player localPlayer;
    private GameCreatorHandler handler;
    private ServerSocket serverSocket;
    private int fieldSize;

    public NetServerGameCreator(String localPlayerName) {
        this.localPlayer = new Player(0, localPlayerName);
    }

    @Override
    public void createGame(int fieldSize, GameCreatorHandler handler) {
        this.handler = handler;
        this.fieldSize = fieldSize;
        (new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        })).start();
    }

    private void startServer() {
        try {
            initializeSocket();
            NetGameConnector netGameConnector = waitForClientConnectAndCreateGameConnector();
            Player opponent = getOpponentPlayer(netGameConnector);
            sendLocalPlayerName(netGameConnector);
            sendFieldSize(netGameConnector);

            this.handler.onGameCreated(new Game(new Field(fieldSize), localPlayer, opponent, netGameConnector, true));
        } catch (IOException e) {
            e.printStackTrace();
            this.handler.onGameCreationFailed();
        } finally {
            this.close();
        }

    }

    private void close() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFieldSize(NetGameConnector netGameConnector) throws IOException {
        netGameConnector.outputStream.writeBytes(fieldSize + "\n");
    }

    private void sendLocalPlayerName(NetGameConnector netGameConnector) throws IOException {
        netGameConnector.outputStream.writeBytes(localPlayer.name + "\n");
    }

    private Player getOpponentPlayer(NetGameConnector netGameConnector) throws IOException {
        Player player = new Player();
        player.id = 1;
        player.name = netGameConnector.reader.readLine();
        return player;
    }

    private NetGameConnector waitForClientConnectAndCreateGameConnector() throws IOException {
        Socket clientSocket = serverSocket.accept();
        return new NetGameConnector(clientSocket);
    }

    private void initializeSocket() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
    }

    public void cancel() {
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
