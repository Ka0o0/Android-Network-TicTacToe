package at.fh_hagenberg.s1520237047.tictactoe;


import android.app.Activity;
import android.app.ProgressDialog;

/**
 * @see <a href="https://raw.githubusercontent.com/firebase/quickstart-android/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/BaseActivity.java">Source Code</a>
 */
public class BaseActivity extends Activity {

    private ProgressDialog progressDialog;

    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

    }

    public void showProgressDialog() {
        showProgressDialog(getString(R.string.loading));
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}