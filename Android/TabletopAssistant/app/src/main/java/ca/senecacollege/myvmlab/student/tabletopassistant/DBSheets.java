package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBSheets {

    /*
     * Accepts a username and prints a JSON object containing the list of the sheets
     */
    void fetchSheetNameList(String username) {

        new fetchSheetNameListTask().execute(username);

    }

    protected class fetchSheetNameListTask extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... username) {
            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            String parameters = "USERNAME=" + username[0]  + "&ANDROID=YES";

            Log.d("doInBackground","Parameters:" + parameters);

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
                Log.d("RESPONSE:",response);

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
                Log.d("TEST",json.toString(4));

                if (json.getString("status").equals("SUCCESS")) {

                    //JSONArray sheetList = new JSONArray(json.getJSONArray("sheet_list")); //this call requires api 19+ (currently using 14)
                    JSONArray sheetList = json.getJSONArray("sheet_list");

                    Log.d("SheetList",sheetList.toString());


                } else if (json.getString("status").equals("FAILURE")) {
                    Log.d("PARSEFAIL",json.getString("msg"));
                }

            } catch (Exception e) {
                Log.e("ParseJSON","",e);
            }

        }

    }

    /*
     * Accepts a username and sheet id (username currently hardcoded in) and returns the contents of the sheet matching the sheet id and belonging to the user
     */
    void fetchSheetById(int id) {

        new fetchSheetByIdTask().execute(Integer.toString(id));

    }

    protected class fetchSheetByIdTask extends AsyncTask <String,Void,String> {

        @Override
        protected String doInBackground(String... id) {
            URL url = null;
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            String response = "";
            String username = "testme123"; //HARDCODED TEMP
            String parameters = "USERNAME=" + username + "&SHEET_ID=" + id[0] + "&ANDROID=YES";

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
                Log.d("TEST",json.toString(4));

                if (json.getString("status").equals("SUCCESS")) {

                    JSONObject sheet = new JSONObject(json.getString("sheet")); //the sheet itself
                    Log.d("TEST",sheet.toString(4));

                } else if (json.getString("status").equals("FAILURE")) {
                    Log.d("PARSEFAIL",json.getString("msg"));
                }


            } catch (Exception e) {
                Log.e("ParseJSON","",e);
            }
        }
    }

}
