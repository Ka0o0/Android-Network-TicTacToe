package at.fh_hagenberg.s1520237047.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "Main";
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = null;

        button = (Button) findViewById(R.id.button_enter);
        button.setOnClickListener(this);
        button = (Button) findViewById(R.id.button_connect_server);
        button.setOnClickListener(this);
        button = (Button) findViewById(R.id.button_start_server);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View _v) {
        switch (_v.getId()) {
            case R.id.button_enter: {
                EditText editText = (EditText) findViewById(R.id.input_name);
                if (editText.getTextSize() < 1)
                    break;
                else {
                    name = editText.getText().toString();
                    editText.clearFocus();
                    //close keyboard
                    InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            }
            case R.id.button_connect_server: {
                //connect with server
                //Test
                Intent i = new Intent(this, Game.class);
                this.startActivity(i);
                break;
            }
            case R.id.button_start_server: {
                //start connection
                break;
            }
            default:
                Log.e(TAG, "an unexpected ID encountered!");
        }
    }
}
