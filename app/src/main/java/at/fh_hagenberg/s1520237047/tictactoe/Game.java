package at.fh_hagenberg.s1520237047.tictactoe;


import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
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

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

@EActivity(R.layout.game)
public class Game extends BaseActivity implements at.fh_hagenberg.s1520237047.tictactoe.service.Game.TicTacToeGameObserver {

    private at.fh_hagenberg.s1520237047.tictactoe.service.Game game;
    GameButton[][] imageButtons;

    @ViewById(R.id.game_grid)
    TableLayout gameGrid;

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
        if (this.game.isRunning()) {
            this.game.close();
        }
    }

    private void initImageButtons() {

//        gameGrid = new LinearLayout();

        for (int i = 0; i < imageButtons.length; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tableRow.setOrientation(TableLayout.HORIZONTAL);
            tableRow.setGravity(Gravity.CENTER);

            FrameLayout secTableRow = new FrameLayout(this);
            secTableRow.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            for (int j = 0; j < imageButtons[i].length; j++) {

                imageButtons[i][j] = createGameButtonForCoordinates(j, i);

                if (j > 0 && j < 3) {
                    View view = new View(this);
                    view.setLayoutParams(new TableRow.LayoutParams(5, ViewGroup.LayoutParams.MATCH_PARENT));
                    view.setBackgroundColor(Color.BLACK);
                    tableRow.addView(view);
                }

                if (i > 0 && i < 3) {
                    View view = new View(this);
                    view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
                    view.setBackgroundColor(Color.BLACK);
                    secTableRow.addView(view);
                }

                tableRow.addView(imageButtons[i][j]);


            }
            gameGrid.setGravity(Gravity.CENTER_HORIZONTAL);
            gameGrid.addView(secTableRow);
            gameGrid.addView(tableRow);
        }
    }

    private GameButton createGameButtonForCoordinates(int x, int y) {
        GameButton temp = (GameButton) getLayoutInflater().inflate(R.layout.game_button_layout, null);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameButtonClick((GameButton) v);
            }
        });
        temp.setBackgroundColor(Color.TRANSPARENT);
        temp.setImageResource(R.drawable.blank);
        temp.setScaleType(ImageView.ScaleType.FIT_CENTER);
        temp.setAdjustViewBounds(true);
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
