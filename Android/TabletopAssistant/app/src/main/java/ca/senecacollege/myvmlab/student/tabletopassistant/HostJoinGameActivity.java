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
import android.widget.Button;
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

        if (!isUser) {
            Button button = (Button) findViewById(R.id.button9);
            button.setEnabled(false);
            button.setAlpha((float) 0.5);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.hostJoinGameActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);

    }

    public void returnToPlayMenu(View v) {
        finish();
    }

    public void openJoinGame(View v) {
        Intent intent = new Intent(this, JoinActivity.class);
        intent.putExtra("USERNAME",username);
        intent.putExtra("ISUSER",true);
        intent.putExtra("SHEET",sheet);

        startActivity(intent);
    }

    public void openHostGame(View v) {
        Intent intent = new Intent(this, HostActivity.class);
        intent.putExtra("USERNAME",username);
        intent.putExtra("ISUSER",true);
        intent.putExtra("SHEET",sheet);

        startActivity(intent);
    }

    public void openLoadCharacter(View v) {
        Intent intent = new Intent(this, loadFromLocalActivity.class);
        startActivity(intent);
    }

    public void openCreateCharacter(View v) {

        DBSheets dbSheets = new DBSheets(this);
        dbSheets.fetchSheetNameList(username); //onPostExecute starts new activity

    }

}