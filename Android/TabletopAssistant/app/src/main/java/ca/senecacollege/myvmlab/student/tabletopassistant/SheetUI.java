package ca.senecacollege.myvmlab.student.tabletopassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SheetUI extends AppCompatActivity {
    RelativeLayout playerLayout;
    float screenWidth, screenHeight, widthOffset, heightOffset;
    String sourceJson, loadType;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_ui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playerLayout = (RelativeLayout) findViewById(R.id.playerLayout);

        context = this;

        loadType = getIntent().getExtras().getString("loadType");
        if ("web".equals(loadType)) {
            try {
                sourceJson = getIntent().getExtras().getString("sheet");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "There was an issue with reading the sheet.", Toast.LENGTH_LONG).show();
                finish();
            }
            parseSheet(sourceJson);
        } else if ("local".equals(loadType)) {
            boolean ok = loadFromLocal(getIntent().getExtras().getString("fileName"));
            Toast.makeText(this, ok ? "Sheet loaded" : "Failed to load!", Toast.LENGTH_LONG).show();
            if (!ok) { finish(); }
        } else {
            Toast.makeText(this, "There was an error processing the action.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sheet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            String newString = generateJson();
            promptCharacterName((String) this.getTitle(), newString);
        }

        return super.onOptionsItemSelected(item);
    }

    public void parseSheet(String json) {
        JSONObject jsonObject;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        widthOffset = screenWidth / 350;
        heightOffset = screenHeight / 500;
        try {
            jsonObject = new JSONObject(json);
            this.setTitle(jsonObject.optString("name"));
            JSONObject blocks = jsonObject.optJSONObject("blocks");
            for (int i = 0; i < blocks.length(); i++) {
                JSONObject block = blocks.getJSONObject("block" + i);
                switch (block.getString("type")) {
                    case "resourceDivLabel":
                        TextView label = new TextView(this);
                        try {
                            label.setText(block.getString("value"));
                            playerLayout.addView(label);
                            label.setX(block.getInt("x") * widthOffset);
                            label.setY(block.getInt("y") * heightOffset);
                            label.setId(i);
                            ViewGroup.LayoutParams params = label.getLayoutParams();
                            params.width = (int) (Integer.parseInt(block.getString("width")
                                    .replace("px", "")) * widthOffset);
                            params.height = (int) (Integer.parseInt(block.getString("height").replace("px", "")) * heightOffset);
                            label.setGravity(Gravity.CENTER);
                            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
                            int newHeight = (int) (params.height + label.getY());
                            if (newHeight > layoutParams.height) {
                                layoutParams.height = newHeight;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "resourceDivVariable":
                        EditText variable = new EditText(this);
                        try {
                            String newText = block.getString("value");
                            if (newText == null || "null".equals(newText)) {
                                newText = "";
                            }
                            variable.setText(newText);
                            playerLayout.addView(variable);
                            variable.setX(block.getInt("x") * widthOffset);
                            variable.setY(block.getInt("y") * heightOffset);
                            variable.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                            variable.setId(i);
                            ViewGroup.LayoutParams params = variable.getLayoutParams();
                            params.width = (int) (Integer.parseInt(block.getString("width").replace("px", "")) * widthOffset);
                            params.height = (int) (Integer.parseInt(block.getString("height").replace("px", "")) * heightOffset);
                            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
                            int newHeight = (int) (params.height + variable.getY());
                            if (newHeight > layoutParams.height) {
                                layoutParams.height = newHeight;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "resourceDivImage":
                        ImageView image = new ImageView(this);
                        try {
                            playerLayout.addView(image);
                            image.setX(block.getInt("x") * widthOffset);
                            image.setY(block.getInt("y") * heightOffset);
                            new DownloadImageTask(image).execute(block.getString("value"));
                            image.setId(i);
                            ViewGroup.LayoutParams params = image.getLayoutParams();
                            params.width = (int) (Integer.parseInt(block.getString("width").replace("px", "")) * widthOffset);
                            params.height = (int) (Integer.parseInt(block.getString("height").replace("px", "")) * heightOffset);
                            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
                            int newHeight = (int) (params.height + image.getY());
                            if (newHeight > layoutParams.height) {
                                layoutParams.height = newHeight;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            JSONObject calculations;
            try {
                calculations = jsonObject.optJSONObject("calculations");
                for (int i = 0; i < calculations.length(); i++) {
                    try {
                        JSONObject calculation = calculations.getJSONObject("" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String generateJson () {
        String json = "";
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(sourceJson);
            JSONObject blocks = jsonObject.optJSONObject("blocks");
            for (int i = 0; i < blocks.length(); i++) {
                JSONObject block = blocks.getJSONObject("block" + i);
                switch (block.getString("type")) {
                    case "resourceDivVariable":
                        try{
                            EditText currentChild = (EditText) playerLayout.getChildAt(i);
                            block.put("value", currentChild.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
            json = jsonObject.toString();
        } catch (Exception e) {
            return "";
        }
        return json;
    }

    public boolean getNewValue (int resourceNumber, String value) {
        try {
            EditText variable = (EditText) findViewById(resourceNumber);
            variable.setText(value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    };

    public boolean changeValue (int resourceNumber) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    };

    public void promptCharacterName (final String fileName, final String json) {
        //Build layout for dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //username
        final TextView characterText = new TextView(this);
        characterText.setText("Character Name:");
        layout.addView(characterText);
        final EditText characterName = new EditText(this);
        layout.addView(characterName);

        //Display dialog
        new AlertDialog.Builder(this)
                .setTitle("Name Character")
                .setView(layout)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                boolean ok = saveToLocal(fileName, json, characterName.getText().toString());
                                Toast.makeText(context, ok ? "Saved" : "Failed to save", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                .show();
    }

    public boolean saveToLocal (String fileName, String json, String characterName) {
        try {
            File directory = new File(getFilesDir(), "/sheets/" + fileName + "/");
            directory.mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(directory, characterName));
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean loadFromLocal (String fileName) {
        try {
            File directory = new File(getFilesDir(), "/sheets/");
            FileInputStream fileInputStream = new FileInputStream(new File(directory, fileName));

            if ( fileInputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                fileInputStream.close();
                sourceJson = stringBuilder.toString();
                parseSheet(sourceJson);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load!", Toast.LENGTH_LONG).show();
            finish();
        }

        return true;
    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }
}

