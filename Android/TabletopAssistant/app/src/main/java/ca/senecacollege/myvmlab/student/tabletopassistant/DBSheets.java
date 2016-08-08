package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBSheets {

    public Context context;
    public String json;
    public String username;

    public DBSheets(Context c) {
        context = c;
    }

    /*
     * Accepts a username and fetches a list of sheets the user created
     * List is then passed to CreateCharacter activity if successful
     *
     * Called when user presses Create Character button
     */
    void fetchSheetNameList(String username) {

        this.username = username;
        new fetchSheetNameListTask().execute(username);

    }
    protected class fetchSheetNameListTask extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... username) {
            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            String parameters = "USERNAME=" + username[0] + "&ANDROID=YES";

            try {
                url = new URL("http://myvmlab.senecacollege.ca:5311/design/sl.php");

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
                    sb.append(line);
                }
                response = sb.toString();
                //Log.d("RESPONSE:",response);

            } catch (Exception e) {
                Log.e("FetchSheetNameList","",e);
                response = null;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject json = new JSONObject(s);
                //Log.d("TEST",json.toString(4));

                if (json.getString("status").equals("SUCCESS")) {

                    //JSONArray sheetList = new JSONArray(json.getJSONArray("sheet_list")); //this call requires api 19+ (currently using 14)
                    JSONArray sheetList = json.getJSONArray("sheet_list");

                    Log.d("SheetList",sheetList.toString());

                    int [] listSheetIds = new int[sheetList.length()];
                    String[] listSheetVersions = new String[sheetList.length()];
                    String[] listSheetNames = new String[sheetList.length()];


                    for (int i = 0; i < sheetList.length(); i++) {
                        JSONObject curr = sheetList.getJSONObject(i);

                        listSheetIds[i] = curr.getInt("sheet_id");
                        listSheetVersions[i] = curr.getString("version");
                        listSheetNames[i] = curr.getString("sheet_name");
                    }

                    Intent intent = new Intent(context,CreateCharacterLoadSheetActivity.class);
                    intent.putExtra("USERNAME",username);
                    intent.putExtra("listSheetIds",listSheetIds);
                    intent.putExtra("listSheetVersions",listSheetVersions);
                    intent.putExtra("listSheetNames",listSheetNames);
                    context.startActivity(intent);

                } else if (json.getString("status").equals("FAILURE")) {
                    String msg = json.getString("msg");

                    Log.d("PARSEFAIL",msg);

                    if (msg.equals("No sheets found.")) {
                        new AlertDialog.Builder(context)
                                .setTitle("NO SHEETS FOUND")
                                .setMessage("You have not created any sheets.\nYou need to create a sheet from the web designer.")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("ERROR")
                                .setMessage("Could not fetch your sheets from server.\nThe server responded: " + msg)
                                .setPositiveButton("OK", null)
                                .show();
                    }


                }

            } catch (Exception e) {
                Log.e("ParseJSON","",e);
            }

        }

    }

    /*
     * Accepts a username and sheet id (username currently hardcoded in) and returns the contents of the sheet matching the sheet id and belonging to the user
     */
    void fetchSheetFromServer(String username, int id) {

        UsernameAndSheetId usernameAndSheetId = new UsernameAndSheetId();
        usernameAndSheetId.username = username;
        usernameAndSheetId.sheetId = id;

        new fetchSheetFromServerTask().execute(usernameAndSheetId);

    }

    private class UsernameAndSheetId {
        public String username;
        public int sheetId;
    }

    protected class fetchSheetFromServerTask extends AsyncTask <UsernameAndSheetId,Void,String> {

        @Override
        protected String doInBackground(UsernameAndSheetId... credentials) {
            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            String parameters = "USERNAME=" + credentials[0].username + "&SHEET_ID=" + credentials[0].sheetId + "&ANDROID=YES";

            Log.d("doInBackground","Parameters:" + parameters);

            try {
                url = new URL("http://myvmlab.senecacollege.ca:5311/design/ds.php");

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
                    sb.append(line);
                }
                response = sb.toString();

            } catch (Exception e) {
                Log.e("FetchSheetNameList","",e);
                response = null;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject json = new JSONObject(s);
                //Log.d("TEST",json.toString(4));

                if (json.getString("status").equals("SUCCESS")) {

                    JSONObject sheet = new JSONObject(json.getString("sheet")); //the sheet itself
                    Log.d("TEST", sheet.toString(4));

                    Intent intent = new Intent(context, SheetUI.class);
                    intent.putExtra("loadType", "web");
                    intent.putExtra("sheet", sheet.toString());

                    context.startActivity(intent);


                } else if (json.getString("status").equals("FAILURE")) {
                    Log.d("PARSEFAIL",json.getString("msg"));
                }


            } catch (Exception e) {
                Log.e("ParseJSON","",e);
            }
        }
    }

}
