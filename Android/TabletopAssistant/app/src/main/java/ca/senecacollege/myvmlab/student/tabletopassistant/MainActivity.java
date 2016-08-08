package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.DialogInterface;
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

        magicTest(); //comment me out and uncomment below
        /*
        new AlertDialog.Builder(this)
                .setTitle("GUEST")
                .setMessage("You are about to play as a guest. Some features may be unavailable unless you create and verify an account and then log in with it.")
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
        */
    }


    /*
     * Temporary function for testing things (currently called by pressing play as guest button)
     */
    void magicTest() {
        Log.e("MAGICTEST","Inside the magic test.");

        String dryRunSheet = "{\"calculations\":{\"1\":{\"targetPlayer\":\"self\",\"value\":\"[HealthVari] + 10\",\"targetVar\":\"HealthVari\",\"name\":\"Heal\"},\"0\":{\"targetPlayer\":\"player\",\"value\":\"@[HealthVari] - ([Attack] - @[Defence])\",\"targetVar\":\"HealthVari\",\"name\":\"Hit\"}},\"height\":\"500px\",\"name\":\"drysheet\",\"blocks\":{\"block5\":{\"childVal\":\"\",\"id\":\"resourceDiv4\",\"height\":\"40px\",\"width\":\"60px\",\"value\":\"Defence\",\"html\":\"<label id=\\\"label2\\\" resourcetype=\\\"label\\\" resourcevalue=\\\"Defence\\\" style=\\\"width: 60px; height: 40px;\\\">Defence<\\/label>\",\"type\":\"resourceDivLabel\",\"y\":\"164\",\"x\":\"35\"},\"block6\":{\"childVal\":\"10\",\"id\":\"resourceDiv5\",\"height\":\"40px\",\"width\":\"40px\",\"value\":\"10\",\"html\":\"<input id=\\\"Defence\\\" placeholder=\\\"Variable value\\\" readonly=\\\"\\\" resourcetype=\\\"variable\\\" resourcevalue=\\\"10\\\" width=\\\"40\\\" height=\\\"40\\\" style=\\\"width: 40px; height: 40px;\\\">\",\"type\":\"resourceDivVariable\",\"y\":\"164\",\"x\":\"135\"},\"block0\":{\"childVal\":\"\",\"id\":\"resourceDiv6\",\"height\":\"100px\",\"width\":\"100px\",\"value\":\"http:\\/\\/myvmlab.senecacollege.ca:5311\\/img\\/placeholder.png\",\"html\":\"<img id=\\\"image0\\\" src=\\\"http:\\/\\/myvmlab.senecacollege.ca:5311\\/img\\/placeholder.png\\\" resourcetype=\\\"image\\\" resourcevalue=\\\"0\\\" width=\\\"100\\\" height=\\\"100\\\" style=\\\"width: 100px; height: 100px;\\\">\",\"type\":\"resourceDivImage\",\"y\":\"0\",\"x\":\"250\"},\"block1\":{\"childVal\":\"\",\"id\":\"resourceDiv0\",\"height\":\"40px\",\"width\":\"55px\",\"value\":\"Health\",\"html\":\"<label id=\\\"label0\\\" resourcetype=\\\"label\\\" resourcevalue=\\\"Health\\\" style=\\\"width: 55px; height: 40px;\\\">Health<\\/label>\",\"type\":\"resourceDivLabel\",\"y\":\"44\",\"x\":\"35\"},\"block2\":{\"childVal\":\"40\",\"id\":\"resourceDiv1\",\"height\":\"40px\",\"width\":\"40px\",\"value\":\"40\",\"html\":\"<input id=\\\"HealthVari\\\" placeholder=\\\"Variable value\\\" readonly=\\\"\\\" resourcetype=\\\"variable\\\" resourcevalue=\\\"40\\\" width=\\\"40\\\" height=\\\"40\\\" style=\\\"width: 40px; height: 40px;\\\">\",\"type\":\"resourceDivVariable\",\"y\":\"44\",\"x\":\"135\"},\"block3\":{\"childVal\":\"\",\"id\":\"resourceDiv2\",\"height\":\"40px\",\"width\":\"60px\",\"value\":\"Attack\",\"html\":\"<label id=\\\"label1\\\" resourcetype=\\\"label\\\" resourcevalue=\\\"Attack\\\" style=\\\"width: 60px; height: 40px;\\\">Attack<\\/label>\",\"type\":\"resourceDivLabel\",\"y\":\"104\",\"x\":\"35\"},\"block4\":{\"childVal\":\"20\",\"id\":\"resourceDiv3\",\"height\":\"40px\",\"width\":\"40px\",\"value\":\"20\",\"html\":\"<input id=\\\"Attack\\\" placeholder=\\\"Variable value\\\" readonly=\\\"\\\" resourcetype=\\\"variable\\\" resourcevalue=\\\"20\\\" width=\\\"40\\\" height=\\\"40\\\" style=\\\"width: 40px; height: 40px;\\\">\",\"type\":\"resourceDivVariable\",\"y\":\"104\",\"x\":\"135\"}}}\n";
        String tempSheet = "{\"blocks\":{\"block0\":{\"x\":\"35\",\"y\":\"63\",\"width\":\"95px\",\"height\":\"40px\",\"type\":\"resourceDivLabel\",\"value\":\"health :\",\"id\":\"resourceDiv0\",\"html\":\"<label resourcevalue=\\\"health :\\\" style=\\\"width: 95px; height: 40px;\\\" resourcetype=\\\"label\\\" id=\\\"label0\\\">health :</label>\",\"childVal\":\"\"},\"block1\":{\"x\":\"155\",\"y\":\"63\",\"width\":\"40px\",\"height\":\"40px\",\"type\":\"resourceDivVariable\",\"value\":\"100\",\"id\":\"resourceDiv1\",\"html\":\"<input style=\\\"width: 40px; height: 40px;\\\" resourcevalue=\\\"100\\\" resourcetype=\\\"variable\\\" readonly=\\\"\\\" placeholder=\\\"Variable value\\\" id=\\\"id_health\\\" width=\\\"40\\\" height=\\\"40\\\">\",\"childVal\":\"100\"},\"block2\":{\"x\":\"35\",\"y\":\"123\",\"width\":\"95px\",\"height\":\"40px\",\"type\":\"resourceDivLabel\",\"value\":\"attack :\",\"id\":\"resourceDiv2\",\"html\":\"<label resourcevalue=\\\"attack :\\\" style=\\\"width: 95px; height: 40px;\\\" resourcetype=\\\"label\\\" id=\\\"label1\\\">attack :</label>\",\"childVal\":\"\"},\"block3\":{\"x\":\"155\",\"y\":\"123\",\"width\":\"40px\",\"height\":\"40px\",\"type\":\"resourceDivVariable\",\"value\":\"20\",\"id\":\"resourceDiv3\",\"html\":\"<input style=\\\"width: 40px; height: 40px;\\\" resourcevalue=\\\"20\\\" resourcetype=\\\"variable\\\" readonly=\\\"\\\" placeholder=\\\"Variable value\\\" id=\\\"id_attack\\\" width=\\\"40\\\" height=\\\"40\\\">\",\"childVal\":\"20\"},\"block4\":{\"x\":\"35\",\"y\":\"183\",\"width\":\"95px\",\"height\":\"40px\",\"type\":\"resourceDivLabel\",\"value\":\"defence :\",\"id\":\"resourceDiv4\",\"html\":\"<label resourcevalue=\\\"defence :\\\" style=\\\"width: 95px; height: 40px;\\\" resourcetype=\\\"label\\\" id=\\\"label2\\\">defence :</label>\",\"childVal\":\"\"},\"block5\":{\"x\":\"155\",\"y\":\"183\",\"width\":\"40px\",\"height\":\"40px\",\"type\":\"resourceDivVariable\",\"value\":\"5\",\"id\":\"resourceDiv5\",\"html\":\"<input style=\\\"width: 40px; height: 40px;\\\" resourcevalue=\\\"5\\\" resourcetype=\\\"variable\\\" readonly=\\\"\\\" placeholder=\\\"Variable value\\\" id=\\\"id_defence\\\" width=\\\"40\\\" height=\\\"40\\\">\",\"childVal\":\"5\"},\"block6\":{\"x\":\"235\",\"y\":\"23\",\"width\":\"100px\",\"height\":\"100px\",\"type\":\"resourceDivImage\",\"value\":\"http://myvmlab.senecacollege.ca:5311/img/placeholder.png\",\"id\":\"resourceDiv6\",\"html\":\"<img style=\\\"width: 100px; height: 100px;\\\" resourcevalue=\\\"0\\\" resourcetype=\\\"image\\\" src=\\\"http://myvmlab.senecacollege.ca:5311/img/placeholder.png\\\" id=\\\"image0\\\" width=\\\"100\\\" height=\\\"100\\\">\",\"childVal\":\"\"}},\"calculations\":{\"0\":{\"value\":\"@[id_health] - ([id_attack] - @[id_defence])\",\"name\":\"Attack\",\"targetVar\":\"id_health\",\"targetPlayer\":\"player\"},\"1\":{\"value\":\"[id_health] + 10\",\"name\":\"Heal\",\"targetVar\":\"id_health\",\"targetPlayer\":\"self\"},\"2\":{\"value\":\"@[id_health] + 3\",\"name\":\"Heal All\",\"targetVar\":\"id_health\",\"targetPlayer\":\"manyPlayers\"},\"3\":{\"value\":\"0\",\"name\":\"Disarm\",\"targetVar\":\"id_attack\",\"targetPlayer\":\"player\"}},\"name\":\"4 Realz\",\"height\":\"500px\"}";

        try {

            CalculationsParser sheet1 = new CalculationsParser(tempSheet);
            CalculationsParser sheet2 = new CalculationsParser(tempSheet);
            CalculationsParser sheet3 = new CalculationsParser(tempSheet);
            CalculationsParser sheet4 = new CalculationsParser(tempSheet);


            sheet1.parseCalculationByName("Attack",sheet4); //player 1 attacks player 4
            sheet2.parseCalculationByName("Attack",sheet4); //player 2 attacks player 4

            sheet4.parseCalculationByName("Heal"); //player 4 heals himself
            sheet4.parseCalculationByName("Disarm",sheet1); //player 4 disarms player 1

            sheet4.parseCalculationByName("Attack",sheet1); //player 4 attacks player 1
            sheet4.parseCalculationByName("Attack",sheet2); //player 4 attacks player 2

            sheet3.parseCalculationByName("Heal All",sheet1,sheet2); //player 3 heals player 1 and 2

            Log.d("Sheet1 health",String.valueOf(sheet1.getBlockValueById("id_health")));
            Log.d("Sheet2 health",String.valueOf(sheet2.getBlockValueById("id_health")));
            Log.d("Sheet3 health",String.valueOf(sheet3.getBlockValueById("id_health")));
            Log.d("Sheet4 health",String.valueOf(sheet4.getBlockValueById("id_health")));
            Log.d("Sheet1 attack",String.valueOf(sheet1.getBlockValueById("id_attack")));
            Log.d("Sheet2 attack",String.valueOf(sheet2.getBlockValueById("id_attack")));
            Log.d("Sheet3 attack",String.valueOf(sheet3.getBlockValueById("id_attack")));
            Log.d("Sheet4 attack",String.valueOf(sheet4.getBlockValueById("id_attack")));


        } catch (Exception e ) {
            Log.e("CalculationsParser","",e);
        }
    }
}


