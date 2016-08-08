package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CreateCharacterLoadSheetActivity extends AppCompatActivity {

    public String username;
    public boolean isUser;

    public ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character_load_sheet);


        Bundle extras = getIntent().getExtras();
        username = extras.getString("Choose a sheet below");

        Toolbar toolbar = (Toolbar) findViewById(R.id.createCharacterToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);

        final int[] listSheetIds = extras.getIntArray("listSheetIds");
        final String[] listSheetVersions = extras.getStringArray("listSheetVersions");
        final String[] listSheetNames = extras.getStringArray("listSheetNames");

        lv = (ListView) findViewById(R.id.sheetListView);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listSheetNames);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String sheetName = ((TextView) view).getText().toString();

                int index = 0;
                while ((!listSheetNames[index].equals(sheetName))&&(index < listSheetNames.length)) {
                    index++;
                }

                if (index < listSheetNames.length) {
                    getSheet(listSheetIds[index]);
                } else {
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("ERROR")
                            .setMessage("Failed to find sheet.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        lv.setAdapter(aa);
    }

    protected void getSheet(int id) {
        DBSheets dbSheets = new DBSheets(this);
        dbSheets.fetchSheetFromServer(username,id);
    }

}
