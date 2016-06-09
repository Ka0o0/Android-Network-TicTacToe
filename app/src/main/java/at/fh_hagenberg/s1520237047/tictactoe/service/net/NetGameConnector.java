package at.fh_hagenberg.s1520237047.tictactoe.service.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.service.GameConnector;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.ConnectionLostException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.OpponentSurrenderedException;

/**
 * @author Kai Takac
 */

public class NetGameConnector extends GameConnector {

    Socket clientSocket;
    InputStream inputStream;
    DataOutputStream outputStream;
    BufferedReader reader;

    public NetGameConnector(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void surrender() throws ConnectionLostException, IOException {
        if (closed)
            throw new IOException("Connector already closed");

        try {
            this.outputStream.writeBytes("surrender\n");
            this.closeConnection();
        } catch (IOException e) {
            this.closeConnection();
            throw new ConnectionLostException();
        }

    }


    private void closeConnection() {
        if (closed)
            return;

        try {
            reader.close();
            reader = null;
        } catch (IOException e) {
        }
        try {
            inputStream.close();
            inputStream = null;
        } catch (IOException e) {
        }
        try {
            outputStream.close();
            outputStream = null;
        } catch (IOException e) {
        }
        try {
            clientSocket.close();
            clientSocket = null;
        } catch (IOException e) {
        }

        this.closed = true;
    }

    @Override
    public Move getOpponentsMove() throws OpponentSurrenderedException, ConnectionLostException, IOException {
        if (closed)
            throw new IOException("Connector already closed");

        String actionName;
        try {
            actionName = this.reader.readLine();
        } catch (IOException e) {
            this.closeConnection();
            throw new ConnectionLostException();
        }

        Move move = null;

        if (actionName.equals("surrender")) {
            this.closeConnection();
            throw new OpponentSurrenderedException();
        } else if (actionName.equals("move")) {
            try {
                String moveLine = this.reader.readLine();
                move = this.parseMoveLine(moveLine);
            } catch (IOException e) {
                this.closeConnection();
                throw new ConnectionLostException();
            }
        }

        return move;
    }

    private Move parseMoveLine(String moveLine) throws IOException {
        String[] splitLine = moveLine.split(";");
        if (splitLine.length != 2) {
            throw new IOException();
        }
        Move move;
        try {
            int x = Integer.parseInt(splitLine[0]);
            int y = Integer.parseInt(splitLine[1]);
            move = new Move(null, x, y);
        } catch (NumberFormatException e) {
            throw new IOException();
        }

        return move;
    }

    @Override
    public void sendMove(Move move) throws ConnectionLostException, IOException {
        if (closed)
            throw new IOException("Connector already closed");

        try {
            this.outputStream.writeBytes("move\n");
            this.outputStream.writeBytes(move.x + ";" + move.y + "\n");
        } catch (IOException e) {
            this.closeConnection();
            throw new ConnectionLostException();
        }
    }

    @Override
    public void close() throws IOException {
        if (closed)
            throw new IOException("Connector already closed");
        this.closeConnection();
    }
}
