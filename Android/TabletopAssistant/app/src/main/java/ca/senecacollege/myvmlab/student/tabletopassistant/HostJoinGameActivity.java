package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class HostJoinGameActivity extends AppCompatActivity {

    public String username;
    public boolean isUser;
    public String sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_join_game);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("USERNAME");
        isUser = extras.getBoolean("ISUSER");
        sheet = extras.getString("SHEET");

        Toolbar toolbar = (Toolbar) findViewById(R.id.hostJoinGameActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);

    }

    protected void returnToPlayMenu(View v) {
        finish();
    }

    protected void openJoinGame(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT
        ).show();

        /*
        if (sheet != null) {

            Intent intent = new Intent(this,JoinGameActivity.class);
            intent.putExtra("USERNAME",username);
            intent.putExtra("ISUSER",true);
            intent.putExtra("SHEET",sheet);

            startActivity(intent);

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Please select a sheet first.")
                    .setPositiveButton("OK",null)
                    .show();
        }
        */
    }

    protected void openHostGame(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT
        ).show();

        /*
        Intent intent = new Intent(this,HostGameActivity.class);
        intent.putExtra("USERNAME",username);
        intent.putExtra("ISUSER",true);
        intent.putExtra("SHEET",sheet);

        startActivity(intent);
        */

    }

    protected void openLoadCharacter(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT
        ).show();
    }

    protected void openCreateCharacter(View v) {

        DBSheets dbSheets = new DBSheets(this);
        dbSheets.fetchSheetNameList(username); //onPostExecute starts new activity

    }

}
