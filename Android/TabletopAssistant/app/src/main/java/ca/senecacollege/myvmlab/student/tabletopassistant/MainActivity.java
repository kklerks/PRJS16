package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    RegisterAndLogin rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl = new RegisterAndLogin(this);

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
                                rl.login(user.getText().toString(),password.getText().toString());
                            }
                        }
                )
                .show();
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
                                rl.createAccount(user.getText().toString(),email.getText().toString(),password.getText().toString(),passwordConfirm.getText().toString());
                            }
                        }
                )
                .show();
    }

    /*
     * User pressed play as guest in menu
     */
    public void buttonPlayAsGuest(View v) {

        new AlertDialog.Builder(this)
                .setTitle("GUEST")
                .setMessage("You are about to play as a guest. Some features may not be available unless you create and verify an account and then log in with it.")
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(getBaseContext(),PlayMenuActivity.class);
                                intent.putExtra("USERNAME","GUEST");
                                intent.putExtra("ISUSER",false); //false for guest
                                startActivity(intent);

                            }
                        }
                )
                .setNegativeButton("Cancel",null)
                .show();

        //magicTest();
    }


    /*
     * Temporary function for testing things (currently called by pressing play as guest button)
     */
    void magicTest() {
        Log.d("MAGICTEST","Inside the magic test.");


        final DBSheets ds = new DBSheets();

        /*
        //TEST1: echos to log the list of sheets belonging to the user
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("TEST")
                .setMessage("Enter a username")
                .setView(et)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ds.fetchSheetNameList(et.getText().toString());
                            }
                        }
                )
                .show();
         */



        //TEST2: echos to log an actual sheet
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView userText = new TextView(this);
        userText.setText("Username:");
        layout.addView(userText);
        final EditText user = new EditText(this);
        layout.addView(user);
        final TextView sheetIdText = new TextView(this);
        sheetIdText.setText("Sheet Id:");
        layout.addView(sheetIdText);
        final EditText sheetId = new EditText(this);
        layout.addView(sheetId);


        new AlertDialog.Builder(this)
                .setTitle("TEST")
                .setMessage("Enter a sheetID belonging to the user")
                .setView(layout)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ds.fetchSheetFromServer(user.getText().toString(), Integer.parseInt(sheetId.getText().toString()));
                            }
                        }
                )
                .show();

    }
}


