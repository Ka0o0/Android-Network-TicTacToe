package at.fh_hagenberg.s1520237047.tictactoe.model;

/**
 * @author Kai Takac
 */

public class Field {

    private Move[][] field;
    private int size;

    public Field(int size) {
        this.field = new Move[size][size];
        this.size = size;
    }

    public Move getMoveAtLocation(int x, int y) {
        return field[y][x];
    }

    public void setMove(Move entry) throws MoveNotPossibleException {
        if (getMoveAtLocation(entry.x, entry.y) != null) {
            throw new MoveNotPossibleException();
        } else if (entry.player == null) {
            throw new IllegalArgumentException("Player must not be null");
        }
        this.field[entry.y][entry.x] = entry;
    }

    public int getSize() {
        return size;
    }
}
