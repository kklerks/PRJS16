package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public String serverResponseJSON; //used for storing server responses
    public String username; //stores the username if the user is logged in


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
     */
    public void login(String user, String password) {
        Log.d("LOGIN","Attempted to login with credentials: User:" + user + " Password:" + password);

        LoginTask lt = new LoginTask(this);
        lt.execute(user,password);

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
     */
    public void createAccount(String user, String email, String password, String passwordConfirm) {
        Log.d("REGISTER","Attempted to register with credentials: User:" + user + " Email:" + email + " Password:" + password + " Confirm password:" + passwordConfirm);

        //TODO: do some client-sided validation here before sending off to server


        RegisterTask rt = new RegisterTask(this);
        rt.execute(user,email,password,passwordConfirm);
    }


    /*
     * User pressed play as guest in menu
     *
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
     * Connects to the network when the user tries to log in
     */
    protected class LoginTask extends AsyncTask <String,Void,String> {

        Context context;
        LoginTask(Context c) {
            context = c;
        }


        @Override
        protected String doInBackground(String... credentials) {
            /*
             * http://stackoverflow.com/questions/4470936/how-to-do-a-http-post-in-android
             */
            Log.d("doInBackground",credentials[0] + '/' + credentials[1]);

            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            //String parameters = "signupUsername=" + credentials[0] + "&signupEmail=" + credentials[1] + "&signupPassword=" + credentials[2] + "&signupPasswordConfirm=" + credentials[3];
            String parameters = "loginUsername=" + credentials[0] + "&loginPassword=" + credentials[1] + "&ANDROID=YES";

            Log.d("doInBackground","Parameters:" + parameters);

            try {
                url = new URL("http://myvmlab.senecacollege.ca:5311/login.php");

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();

                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    //Log.d("FROMWEBSITE:",line);
                    sb.append(line);
                }
                response = sb.toString();
                //Log.d("RESPONSE:",response);

            } catch (Exception e) {
                Log.e("LOGIN","",e);
                response = null;
            }

            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("PHP (onPostExecute)", s);
            Context c = context;
            serverResponseJSON = s;

            try {

                JSONObject json = new JSONObject(serverResponseJSON);
                String status = json.getString("status");
                //Log.d("STATUS", status + ' ' + serverResponseJSON);

                if (status.equals("SUCCESS")) {

                    Log.d("LOGIN","Successfully logged in");
                    username = json.getString("username");

                    //Open PlayMenuActivity
                    Intent intent = new Intent(getBaseContext(),PlayMenuActivity.class);
                    intent.putExtra("USERNAME",username);
                    intent.putExtra("ISUSER",true);
                    startActivity(intent);

                } else if (status.equals("FAILURE")) {
                    String msg = json.getString("msg");
                    new AlertDialog.Builder(c)
                            .setTitle("ERROR")
                            .setMessage("There was an error processing your request.\n\nThe server responded:\n" + msg)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    new AlertDialog.Builder(c)
                            .setTitle("ERROR")
                            .setMessage("There was an error processing your request.\nPlease try again later.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            } catch (Exception e) {
                Log.e("PARSE_RESPONSE","",e);
            }
        }


    }


    /*
     * Connects to the network when the user tries to register an account
     */
    protected class RegisterTask extends AsyncTask <String,Void,String> {

        Context context;
        RegisterTask(Context c) {
            context = c;
        }

        @Override
        protected String doInBackground(String... credentials) {
            /*
             * http://stackoverflow.com/questions/4470936/how-to-do-a-http-post-in-android
             */
            Log.d("doInBackground",credentials[0] + '/' + credentials[1] + '/' + credentials[2]);

            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            //String parameters = "signupUsername=" + credentials[0] + "&signupEmail=" + credentials[1] + "&signupPassword=" + credentials[2] + "&signupPasswordConfirm=" + credentials[3];
            String parameters = "signupUsername=" + credentials[0] + "&signupEmail=" + credentials[1] + "&signupPassword=" + credentials[2] + "&ANDROID=YES";

            Log.d("doInBackground","Parameters:" + parameters);

            try {
                url = new URL("http://myvmlab.senecacollege.ca:5311/register.php");

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();

                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    //Log.d("FROMWEBSITE:",line);
                    sb.append(line);
                }
                response = sb.toString();
                Log.d("RESPONSE:",response);

            } catch (Exception e) {
                Log.e("REGISTER","",e);
                response = null;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("PHP (onPostExecute)", s);
            Context c = context;
            serverResponseJSON = s;

            try {

                JSONObject json = new JSONObject(serverResponseJSON);
                String status = json.getString("status");
                Log.d("STATUS",status + ' ' + serverResponseJSON);

                if (status.equals("SUCCESS")) {
                    new AlertDialog.Builder(c)
                            .setTitle("SUCCESS")
                            .setMessage("You have successfully created your account... please check your email to confirm your account before signing in.")
                            .setPositiveButton("OK",null)
                            .show();
                } else if (status.equals("FAILURE")) {
                    String msg = json.getString("msg");
                    new AlertDialog.Builder(c)
                            .setTitle("ERROR")
                            .setMessage("There was an error processing your request.\n\nThe server responded:\n" + msg)
                            .setPositiveButton("OK",null)
                            .show();
                } else {
                    new AlertDialog.Builder(c)
                            .setTitle("ERROR")
                            .setMessage("There was an error processing your request.\nPlease try again later.")
                            .setPositiveButton("OK",null)
                            .show();
                }

            } catch (Exception e) {
                Log.e("PARSE_RESPONSE","",e);
            }

        }

    }

    void magicTest() {
        Log.d("MAGICTEST","Inside the magic test.");
        /*
         * Temporary function for testing things (currently called by pressing play as guest button)
         */

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


