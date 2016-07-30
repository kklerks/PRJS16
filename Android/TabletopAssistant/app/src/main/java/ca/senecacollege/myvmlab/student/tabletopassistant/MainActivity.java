package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.TestMethod;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*
     * User pressed login button in menu
     * Display a dialog for logging in
     */
    public void buttonLogin(View v) {
        //Build layout for dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //username
        final TextView userText = new TextView(this);
        userText.setText("Username:");
        layout.addView(userText);
        final EditText user = new EditText(this);
        layout.addView(user);
        //password
        final TextView passwordText = new TextView(this);
        passwordText.setText("Password");
        layout.addView(passwordText);
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(password);

        //Display dialog
        new AlertDialog.Builder(this)
                .setTitle("LOG IN")
                .setView(layout)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Log in",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                login(user.getText().toString(),password.getText().toString());
                            }
                        }
                )
                .show();
    }

    /*
     * User pressed login in the login dialog
     * Returns true if account was able to be created, false otherwise
     */
    public boolean login(String user, String password) {
        Log.d("LOGIN","Attempted to login with credentials: User:" + user + " Password:" + password);

        return true; //TEMP
    }

    /*
     * User pressed register button in menu
     * Display a dialog for registering a new account
     */
    public void buttonRegister(View v) {
        //Build layout for dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //username
        final TextView userText = new TextView(this);
        userText.setText("Username:");
        layout.addView(userText);
        final EditText user = new EditText(this);
        layout.addView(user);
        //email
        final TextView emailText = new TextView(this);
        emailText.setText("Email:");
        layout.addView(emailText);
        final EditText email = new EditText(this);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS); //not needed?
        layout.addView(email);
        //password
        final TextView passwordText = new TextView(this);
        passwordText.setText("Password");
        layout.addView(passwordText);
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(password);
        //confirm password
        final TextView passwordConfirmText = new TextView(this);
        passwordConfirmText.setText("Confirm password:");
        layout.addView(passwordConfirmText);
        final EditText passwordConfirm = new EditText(this);
        passwordConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(passwordConfirm);

        //Display dialog
        new AlertDialog.Builder(this)
                .setTitle("REGISTER ACCOUNT")
                .setView(layout)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Register",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                createAccount(user.getText().toString(),email.getText().toString(),password.getText().toString(),passwordConfirm.getText().toString());
                            }
                        }
                )
                .show();
    }

    /*
     * User pressed register in the register dialog
     *
     * Returns true if account was able to be created, false otherwise
     */
    public boolean createAccount(String user, String email, String password, String passwordConfirm) {
        Log.d("REGISTER","Attempted to register with credentials: User:" + user + " Email:" + email + " Password:" + password + " Confirm password:" + passwordConfirm);

        /*
         * http://stackoverflow.com/questions/4470936/how-to-do-a-http-post-in-android
         * TODO: this probably all needs to be moved to another activity with asynctask to work
         */

        HttpURLConnection connection;
        OutputStreamWriter request = null;
        String response = "";
        String parameters = "signupUsername="+user+"&signupPassword"+password+"&signupEmail"+email;

        try {
            URL url = new URL("http://student.myvmlab.senecacollege.ca:5311/register.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream()); //TODO: (crashes over here)
            request.write(parameters);
            request.flush();
            request.close();

            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d("PHP",response);

        return true; //TEMP
    }


    /*
     * User pressed play as guest in menu
     *
     */
    public void buttonPlayAsGuest(View v) {
        Log.d("BUTTON","Pressed play as guest.");
        Toast.makeText(getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT).show();
    }

}
