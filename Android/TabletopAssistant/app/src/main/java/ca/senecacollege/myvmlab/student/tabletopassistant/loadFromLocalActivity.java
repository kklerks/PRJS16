package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class loadFromLocalActivity extends AppCompatActivity {

    ListView fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_from_local);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            File sheetDirectory = new File(getFilesDir() + "/sheets/");

            File[] files = sheetDirectory.listFiles();

            fileList = (ListView) findViewById(R.id.fileList);

            ArrayList<String> fileNames = new ArrayList<>();

            for (File file : files) {
                String fileName = Uri.fromFile(file).getLastPathSegment();
                fileNames.add(fileName);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);

            fileList.setAdapter(arrayAdapter);

            fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    TextView textView = (TextView) view;
                    textView.getText();
                    loadSheet((String) textView.getText());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No sheets found!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public boolean loadSheet (String fileName) {
        Intent intent = new Intent(this, SheetUI.class);
        intent.putExtra("loadType", "local");
        intent.putExtra("fileName", fileName);
        startActivity(intent);
        return true;
    };

}
