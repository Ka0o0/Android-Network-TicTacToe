package at.fh_hagenberg.s1520237047.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import at.fh_hagenberg.s1520237047.tictactoe.model.Field;
import at.fh_hagenberg.s1520237047.tictactoe.model.Player;
import at.fh_hagenberg.s1520237047.tictactoe.service.*;
import at.fh_hagenberg.s1520237047.tictactoe.service.Game;
import at.fh_hagenberg.s1520237047.tictactoe.service.net.NetClientGameCreator;
import at.fh_hagenberg.s1520237047.tictactoe.service.net.NetServerGameCreator;
import commons.validator.routines.InetAddressValidator;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements GameCreator.GameCreatorHandler {

    public static final String TAG = "Main";
    private static final String PLAYER_NAME_PREFERENCE_KEY = "TicTacToePlayerName";


    @NotEmpty(messageResId = R.string.name_empty)
    @ViewById(R.id.input_name)
    public TextView nameTextView;

    private Validator validator;
    SharedPreferences sharedPreferences;

    Handler handler = new Handler();

    private NetServerGameCreator netServerGameCreator;
    private Dialog currentDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
    }

    @AfterViews
    public void init() {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        showSavedPlayerName();
    }

    private void showSavedPlayerName() {
        nameTextView.setText(sharedPreferences.getString(PLAYER_NAME_PREFERENCE_KEY, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (netServerGameCreator != null) {
            netServerGameCreator.cancel();
        }
    }

    @Click(R.id.button_connect_server)
    public void showConnectToServerDialog() {
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.enter_ip_address));
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectToServer(input.getText().toString());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                currentDialog = builder.show();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                showValidationError(errors);
            }
        });
        validator.validate();
    }

    private void connectToServer(final String text) {
        if (!InetAddressValidator.getInstance().isValidInet4Address(text)) {
            Toast.makeText(MainActivity.this, getString(R.string.wrong_ip_address), Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        try {
            NetClientGameCreator gameCreator = new NetClientGameCreator(nameTextView.getText().toString(), InetAddress.getByName(text));
            gameCreator.createGame(3, MainActivity.this);
        } catch (UnknownHostException e) {
            Toast.makeText(MainActivity.this, getString(R.string.wrong_ip_address), Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }
    }

    private String getFormattedIpAddress() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }

    @Click(R.id.button_start_server)
    public void showStartServerDialog() {
        /*Game game = new Game(new Field(3), new Player(0, "Test"), new Player(1, "gegner"), null, true);
        GlobalGame.getInstance().setGame(game);
        Game_.intent(this).start();*/
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                startServer();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.wait_for_connection));
                TextView textView = new TextView(MainActivity.this);
                textView.setText(getString(R.string.connect_to_ip, getFormattedIpAddress()));
                builder.setView(textView);
                builder.setCancelable(false);

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelServer();
                        dialog.dismiss();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancelServer();
                    }
                });
                currentDialog = builder.show();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                showValidationError(errors);
            }
        });
        validator.validate();
    }

    private void startServer() {
        netServerGameCreator = new NetServerGameCreator(this.nameTextView.getText().toString());
        netServerGameCreator.createGame(3, this);
    }

    private void savePlayerName() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_NAME_PREFERENCE_KEY, String.valueOf(this.nameTextView.getText()));
        editor.apply();
    }

    private void cancelServer() {
        if (netServerGameCreator != null) {
            netServerGameCreator.cancel();
        }
    }

    public void showValidationError(List<ValidationError> errors) {
        Toast.makeText(this, errors.get(0).getCollatedErrorMessage(this), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGameCreated(Game game) {
        hideProgressDialog();
        savePlayerName();
        GlobalGame.getInstance().setGame(game);
        Game_.intent(this).startForResult(0);
    }

    @Override
    public void onGameCreationFailed() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentDialog != null) {
                    currentDialog.hide();
                }
                hideProgressDialog();
                Toast.makeText(MainActivity.this, getString(R.string.game_creation_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
