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

    private boolean compareFieldPlayer(int x1, int y1, int x2, int y2) {
        return field[x1][y1] != null && field[x2][y2] != null && field[x1][y1].player.id == field[x2][y2].player.id;
    }

    public Player getWinner() {
        //horizontal
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length - 2; j++) {
                if (compareFieldPlayer(i, j, i, j + 1) && compareFieldPlayer(i, j, i, j + 2)) {
                    return field[i][j].player;
                }
            }
        }

        for (int i = 0; i < field.length - 2; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (compareFieldPlayer(i, j, i + 1, j) && compareFieldPlayer(i, j, i + 2, j)) {
                    return field[i][j].player;
                }
            }
        }

        for (int i = 0; i < field.length - 2; i++) {
            for (int j = 0; j < field[i].length - 2; j++) {
                if (compareFieldPlayer(i, j, i + 1, j + 1) && compareFieldPlayer(i, j, i + 2, j + 2)) {
                    return field[i][j].player;
                }
            }
        }

        return null;
    }

    public boolean isFull() {
        boolean full = true;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == null) {
                    full = false;
                    break;
                }
            }
        }
        return full;
    }
}
