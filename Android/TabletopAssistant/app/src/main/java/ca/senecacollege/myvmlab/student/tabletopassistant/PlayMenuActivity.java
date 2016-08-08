package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Intent;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PlayMenuActivity extends AppCompatActivity {

    public String username;
    public boolean isUser; //false if guest

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_menu);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("USERNAME");
        isUser = extras.getBoolean("ISUSER");

        if (!isUser) {
            Button logoutButton = (Button) findViewById(R.id.button4);
            logoutButton.setEnabled(false);
            logoutButton.setAlpha((float) 0.5);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.playMenuActivitytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);

    }

    /*
     * Called when user presses back button in menu
     */
    @Override
    public void onBackPressed() {
        logout();
    }

    /*
     * Called when logout button is pressed
     */
    public void logoutPrompt(View v) {
        logout();
    }

    public void logout() {
        new AlertDialog.Builder(this)
                .setTitle("LOGOUT")
                .setMessage("Log out and return to the main menu?")
                .setPositiveButton("Logout",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish(); //returns to main activity
                            }
                        }
                )
                .setNegativeButton("Cancel",null)
                .show();
    }


    public void openTutorial(View v) {

        Toast.makeText(
                getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT
        ).show();
    }

    public void openOptions(View v) {

        Toast.makeText(
                getApplicationContext(),
                "Not implemented yet.",
                Toast.LENGTH_SHORT
        ).show();
    }

    public void openPlay(View v) {

        //Open PlayMenuActivity
        Intent intent = new Intent(this,HostJoinGameActivity.class);
        intent.putExtra("USERNAME",username);
        intent.putExtra("ISUSER", isUser);
        startActivity(intent);

    }

}
