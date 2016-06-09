package at.fh_hagenberg.s1520237047.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Gabriel on 09.06.16.
 */
public class GameBoard extends Activity {

    Game game = new Game();

    public int[][] field = new int[3][3];
    boolean player = false; //false = Player 1 else Player 2
    private Context mContext;

    GameBoard(Context context) {
        mContext = context;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = 0;
            }
        }
    }

    public boolean startGame(int row, int button) {
        if (field[row][button] != 0)
            return false;
        if (player == false) {
            field[row][button] = 1;
            player = true;
        } else {
            field[row][button] = 2;
            player = false;
        }
        if (full()) {
            Toast.makeText(mContext, "Unentschieden!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (success()) {
            String string;
            if (player)
                string = mContext.getResources().getString(R.string.player_1_success);
            else
                string = mContext.getResources().getString(R.string.player_2_success);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(string + "\n" +mContext.getResources().getString(R.string.success_question))
                    .setCancelable(false)
                    .setPositiveButton(R.string.success_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new GameBoard(mContext);
                            Intent i = new Intent(mContext, Game.class);
                            mContext.startActivity(i);
                        }
                    })
                    .setNegativeButton(R.string.success_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(mContext, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(i);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return true;
    }

    private boolean success() {
        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != 0)
            return true;

        if (field[2][0] == field[1][1] && field[2][0] == field[0][2] && field[2][0] != 0)
            return true;

        for (int i = 0; i < 3; i++) {
            if (field[i][0] == field[i][1] && field[i][1] == field[i][2] && field[i][0] != 0)
                return true;
            if (field[0][i] == field[1][i] && field[1][i] == field[2][i] && field[0][i] != 0)
                return true;
        }

        return false;
    }

    private boolean full() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] == 0)
                    return false;
            }
        }
        return true;
    }


}
