package at.fh_hagenberg.s1520237047.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Gabriel on 09.06.16.
 */
public class Game extends Activity implements View.OnClickListener {

    private GameBoard gameBoard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        ImageButton b = null;

        b = (ImageButton) findViewById(R.id.row1_button1);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row1_button2);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row1_button3);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row2_button1);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row2_button2);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row2_button3);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row3_button1);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row3_button2);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.row3_button3);
        b.setOnClickListener(this);

        gameBoard = new GameBoard(this);
    }

    @Override
    public void onClick(View _v) {
        switch (_v.getId()) {
            case R.id.row1_button1: {
                if (gameBoard.startGame(0, 0))
                    setPic(R.id.row1_button1);
            }
            break;
            case R.id.row1_button2: {
                if (gameBoard.startGame(0, 1))
                    setPic(R.id.row1_button2);
            }
            break;
            case R.id.row1_button3: {
                if (gameBoard.startGame(0, 2))
                    setPic(R.id.row1_button3);
            }
            break;
            case R.id.row2_button1: {
                if (gameBoard.startGame(1, 0))
                    setPic(R.id.row2_button1);
            }
            break;
            case R.id.row2_button2: {
                if (gameBoard.startGame(1, 1))
                    setPic(R.id.row2_button2);
            }
            break;
            case R.id.row2_button3: {
                if (gameBoard.startGame(1, 2))
                    setPic(R.id.row2_button3);
            }
            break;
            case R.id.row3_button1: {
                if (gameBoard.startGame(2, 0))
                    setPic(R.id.row3_button1);
            }
            break;
            case R.id.row3_button2: {
                if (gameBoard.startGame(2, 1))
                    setPic(R.id.row3_button2);
            }
            break;
            case R.id.row3_button3: {
                if (gameBoard.startGame(2, 2))
                    setPic(R.id.row3_button3);
            }
            break;
        }
    }

    private void setPic(int ressource) {
        ImageButton imageButton = (ImageButton) findViewById(ressource);
        if (gameBoard.player == false) {
            imageButton.setImageResource(R.drawable.cross);
        } else
            imageButton.setImageResource(R.drawable.circle);

    }
}
