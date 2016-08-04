package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterAndLogin {

    public String serverResponseJSON; //used for storing server responses
    public String username; //stores the username if the user is logged in
    public Context context; //used to display dialogs, toasts and to send intents

    public RegisterAndLogin(Context c) {
        context = c;
    }

    /*
     * User pressed register in the register dialog
     */
    public void createAccount(String user, String email, String password, String passwordConfirm) {
        Log.d("REGISTER","Attempted to register with credentials: User:" + user + " Email:" + email + " Password:" + password + " Confirm password:" + passwordConfirm);

        if (password.equals(passwordConfirm)) {
            RegisterTask rt = new RegisterTask();
            rt.execute(user, email, password, passwordConfirm);
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("ERROR")
                    .setMessage("There was an error processing your request.\n\nPasswords do not match.\n")
                    .setPositiveButton("OK",null)
                    .show();
        }
    }

    /*
     * Connects to the network when the user tries to register an account
     */
    protected class RegisterTask extends AsyncTask <String,Void,String> {

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
            serverResponseJSON = s;

            try {

                JSONObject json = new JSONObject(serverResponseJSON);
                String status = json.getString("status");
                Log.d("STATUS",status + ' ' + serverResponseJSON);

                if (status.equals("SUCCESS")) {
                    new AlertDialog.Builder(context)
                            .setTitle("SUCCESS")
                            .setMessage("You have successfully created your account... please check your email to confirm your account before signing in.")
                            .setPositiveButton("OK",null)
                            .show();
                } else if (status.equals("FAILURE")) {
                    String msg = json.getString("msg");
                    new AlertDialog.Builder(context)
                            .setTitle("ERROR")
                            .setMessage("There was an error processing your request.\n\nThe server responded:\n" + msg)
                            .setPositiveButton("OK",null)
                            .show();
                } else {
                    new AlertDialog.Builder(context)
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

    /*
     * User pressed login in the login dialog
     */
    public void login(String user, String password) {
        Log.d("LOGIN","Attempted to login with credentials: User:" + user + " Password:" + password);

        LoginTask lt = new LoginTask();
        lt.execute(user,password);

    }

    /*
     * Connects to the network when the user tries to log in
     */
    protected class LoginTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... credentials) {
            /*
             * http://stackoverflow.com/questions/4470936/how-to-do-a-http-post-in-android
             */
            //Log.d("doInBackground",credentials[0] + '/' + credentials[1]);

            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            //String parameters = "signupUsername=" + credentials[0] + "&signupEmail=" + credentials[1] + "&signupPassword=" + credentials[2] + "&signupPasswordConfirm=" + credentials[3];
            String parameters = "loginUsername=" + credentials[0] + "&loginPassword=" + credentials[1] + "&ANDROID=YES";

            //Log.d("doInBackground","Parameters:" + parameters);

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
            //Log.d("PHP (onPostExecute)", s);

            serverResponseJSON = s;

            try {

                JSONObject json = new JSONObject(serverResponseJSON);
                String status = json.getString("status");
                //Log.d("STATUS", status + ' ' + serverResponseJSON);

                if (status.equals("SUCCESS")) {

                    Log.d("LOGIN","Successfully logged in");
                    username = json.getString("username");

                    //Open PlayMenuActivity
                    Intent intent = new Intent(context,PlayMenuActivity.class);
                    intent.putExtra("USERNAME",username);
                    intent.putExtra("ISUSER",true);
                    context.startActivity(intent);

                } else if (status.equals("FAILURE")) {
                    String msg = json.getString("msg");
                    new AlertDialog.Builder(context)
                            .setTitle("ERROR")
                            .setMessage("There was an error processing your request.\n\nThe server responded:\n" + msg)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    new AlertDialog.Builder(context)
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
}
