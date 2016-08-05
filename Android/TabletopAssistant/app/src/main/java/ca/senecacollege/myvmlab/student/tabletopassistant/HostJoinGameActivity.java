package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class HostJoinGameActivity extends AppCompatActivity {

    public String username;
    public boolean isUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_join_game);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("USERNAME");
        isUser = extras.getBoolean("ISUSER");

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
    }

    protected void openHostGame(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT
        ).show();
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
