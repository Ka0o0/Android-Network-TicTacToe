package at.fh_hagenberg.s1520237047.tictactoe.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kai Takac
 */
public class FieldTest {

    @Test
    public void setMove() throws MoveNotPossibleException {
        Field field = new Field(10);
        Move move = new Move(new Player(0, "Test User"), 2, 2);
        field.setMove(move);
        assertEquals(move, field.getMoveAtLocation(2, 2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullPlayerSetMove() throws MoveNotPossibleException {
        Field field = new Field(10);
        Move move = new Move(null, 2, 2);
        field.setMove(move);
    }

    @Test(expected=MoveNotPossibleException.class)
    public void testDoubleSetMove() throws MoveNotPossibleException {
        Field field = new Field(10);
        Move move = new Move(new Player(0, "Test User"), 2, 2);
        field.setMove(move);
        field.setMove(move);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testOutOfBoundsSetMove() throws MoveNotPossibleException {
        Field field = new Field(10);
        Move move = new Move(new Player(0, "Test User"), 12, 12);
        field.setMove(move);
    }

}