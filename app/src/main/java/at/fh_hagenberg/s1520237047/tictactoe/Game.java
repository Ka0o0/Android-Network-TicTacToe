package at.fh_hagenberg.s1520237047.tictactoe;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import at.fh_hagenberg.s1520237047.tictactoe.model.Move;
import at.fh_hagenberg.s1520237047.tictactoe.model.MoveNotPossibleException;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.GlobalGame;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.NoObserverSetException;
import at.fh_hagenberg.s1520237047.tictactoe.service.exceptions.NotConnectedException;

@EActivity(R.layout.game)
public class Game extends BaseActivity implements at.fh_hagenberg.s1520237047.tictactoe.service.Game.TicTacToeGameObserver {

    private at.fh_hagenberg.s1520237047.tictactoe.service.Game game;
    GameButton[][] imageButtons;

    @ViewById(R.id.game_grid)
    LinearLayout gameGrid;

    Handler handler = new Handler();

    @AfterViews
    public void init() {
        this.game = GlobalGame.getInstance().getGame();
        if (this.game == null) {
            showMain();
        }
        this.game.setObserver(this);
        int fieldSize = this.game.getField().getSize();
        imageButtons = new GameButton[fieldSize][fieldSize];
        initImageButtons();
        checkMove();
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.close();
    }

    private void close() {
        if(this.game.isRunning()){
            this.game.close();
        }
    }

    private void initImageButtons() {
        for (int i = 0; i < imageButtons.length; i++) {
            LinearLayout horizontalLinearLayout = new LinearLayout(this);
            horizontalLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < imageButtons[i].length; j++) {


                imageButtons[i][j] = createGameButtonForCoordinates(j, i);
                horizontalLinearLayout.addView(imageButtons[i][j]);

            }
            gameGrid.addView(horizontalLinearLayout);
        }
    }

    private GameButton createGameButtonForCoordinates(int x, int y) {
        GameButton temp = (GameButton) getLayoutInflater().inflate(R.layout.game_button_layout, null);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameButtonClick((GameButton)v);
            }
        });
        temp.setBackgroundColor(Color.RED);
        temp.setGameX(x);
        temp.setGameY(y);
        return temp;
    }

    private void checkMove() {
        if (game.isMyTurn()) {
            showYourTurnHint();
        } else {
            waitForOpponent();
        }
    }

    private void showYourTurnHint() {
        Toast.makeText(this, getString(R.string.your_turn), Toast.LENGTH_SHORT).show();
    }

    private void waitForOpponent() {
        try {
            game.waitForOpponentsMove();
            showProgressDialog(getString(R.string.waiting_for_opponent));
        } catch (NotConnectedException | MoveNotPossibleException e) {
            onConnectionClosed(this.game);
        } catch (NoObserverSetException e) {
            e.printStackTrace();
        }
    }


    public void onGameButtonClick(GameButton gameButton) {
        if (this.game.isMyTurn()) {
            try {
                this.game.playMove(gameButton.getGameX(), gameButton.getGameY());
            } catch (MoveNotPossibleException e) {
                Toast.makeText(this, getString(R.string.move_not_possible), Toast.LENGTH_SHORT).show();
            } catch (NotConnectedException e) {
                onConnectionClosed(this.game);
            } catch (NoObserverSetException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPic(final Move move) {
        GameButton gameButton = imageButtons[move.y][move.x];
        if (move.player.id == 0) {
            gameButton.setImageResource(R.drawable.cross);
        } else
            gameButton.setImageResource(R.drawable.circle);
    }

    @Override
    public void onMyMoveComplete(at.fh_hagenberg.s1520237047.tictactoe.service.Game game, final Move move) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMove(move);
                waitForOpponent();
            }
        });
    }

    private void showMove(Move move) {
        setPic(move);
    }

    @Override
    public void onSurrenderSuccessful(at.fh_hagenberg.s1520237047.tictactoe.service.Game game) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMain();
            }
        });
    }

    @Override
    public void onOpponentsMoveComplete(at.fh_hagenberg.s1520237047.tictactoe.service.Game game, final Move move) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMove(move);
                hideProgressDialog();
                showYourTurnHint();
            }
        });
    }

    @Override
    public void onOpponentSurrendered(at.fh_hagenberg.s1520237047.tictactoe.service.Game game, Player player) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMain();
                Toast.makeText(Game.this, getString(R.string.opponent_surrendered), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMain() {
        MainActivity_.intent(this).start();
    }

    @Override
    public void onConnectionClosed(at.fh_hagenberg.s1520237047.tictactoe.service.Game game) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMain();
                Toast.makeText(Game.this, getString(R.string.connection_closed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMatchDraw(at.fh_hagenberg.s1520237047.tictactoe.service.Game game) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMain();
                Toast.makeText(Game.this, getString(R.string.match_draw), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMatchWin(at.fh_hagenberg.s1520237047.tictactoe.service.Game game, final Player winner) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMain();
                Toast.makeText(Game.this, getString(R.string.player_wins, winner.name), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
