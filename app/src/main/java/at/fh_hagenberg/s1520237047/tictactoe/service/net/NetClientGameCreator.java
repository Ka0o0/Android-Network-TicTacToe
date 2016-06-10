package at.fh_hagenberg.s1520237047.tictactoe.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import at.fh_hagenberg.s1520237047.tictactoe.model.Field;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.Game;
import at.fh_hagenberg.s1520237047.tictactoe.service.GameCreator;

/**
 * @author Kai Takac
 */

public class NetClientGameCreator implements GameCreator {


    private final Player localPlayer;
    private final InetAddress endPoint;
    private GameCreatorHandler handler;
    private int fieldSize;

    public NetClientGameCreator(final String playerName, final InetAddress endpoint) {
        this.localPlayer = new Player(1, playerName);
        this.endPoint = endpoint;
    }

    @Override
    public void createGame(int fieldSize, GameCreatorHandler handler) {
        this.handler = handler;
        this.fieldSize = fieldSize;
        (new Thread(new Runnable() {
            @Override
            public void run() {
                connectToServer();
            }
        })).start();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(endPoint, NetServerGameCreator.SERVER_PORT);
            NetGameConnector netGameConnector = new NetGameConnector(socket);
            sendLocalPlayerName(netGameConnector);
            Player opponent = getOpponentPlayer(netGameConnector);
            this.fieldSize = getFieldSize(netGameConnector);

            this.handler.onGameCreated(new Game(new Field(fieldSize), localPlayer, opponent, netGameConnector, false));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            this.handler.onGameCreationFailed();
        }
    }

    private int getFieldSize(NetGameConnector netGameConnector) throws IOException {
        return Integer.parseInt(netGameConnector.reader.readLine());
    }

    private Player getOpponentPlayer(NetGameConnector netGameConnector) throws IOException {
        Player player = new Player();
        player.id = 0;
        player.name = netGameConnector.reader.readLine();
        return player;
    }

    private void sendLocalPlayerName(NetGameConnector netGameConnector) throws IOException {
        netGameConnector.outputStream.writeBytes(localPlayer.name + "\n");
    }
}
