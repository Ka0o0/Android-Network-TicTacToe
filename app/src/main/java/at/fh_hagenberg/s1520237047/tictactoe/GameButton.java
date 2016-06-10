package at.fh_hagenberg.s1520237047.tictactoe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * @author Kai Takac
 */

public class GameButton extends ImageButton {
    private int gameX;
    private int gameY;

    public GameButton(Context context) {
        super(context);
    }

    public GameButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getGameX() {
        return gameX;
    }

    public int getGameY() {
        return gameY;
    }

    public void setGameX(int gameX) {
        this.gameX = gameX;
    }

    public void setGameY(int gameY) {
        this.gameY = gameY;
    }
}
