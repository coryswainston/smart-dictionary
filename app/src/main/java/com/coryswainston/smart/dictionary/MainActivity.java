package com.coryswainston.smart.dictionary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.google.android.gms.vision.Frame.ROTATION_90;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText lookup;
    private TextView definition;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        textView = (TextView)findViewById(R.id.textView);
        lookup = (EditText)findViewById(R.id.lookup);
        definition = (TextView)findViewById(R.id.definition);

        textView.setMovementMethod(new ScrollingMovementMethod());
        definition.setMovementMethod(new ScrollingMovementMethod());

        Button button = (Button)findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Editable s = lookup.getText();
                Log.d("The word is", s.toString().toLowerCase());
                new CallbackTask().execute("https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" +
                        s.toString().toLowerCase());
            }
        });
        lookup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.d("The word is", s.toString().toLowerCase());
//                new CallbackTask().execute("https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" +
//                        s.toString().toLowerCase());
            }
        });

        if (!hasPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)) {
            Log.d("permissions", "we don't have em");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},0);
        } else {
            openCamera();
        }
    }

    private boolean hasPermissions(String ... permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (!(ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        Log.d("permissions", "we have em all");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != 0) {
            return;
        }
        if (results.length > 2 && results[0] == PackageManager.PERMISSION_GRANTED
                && results[1] == PackageManager.PERMISSION_GRANTED && results[2] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            imageUri = getUri(String.valueOf(new Date().getTime()), ".jpg");
        }
        catch (Exception e) {
            Log.e("MainActivity", "unable to get uri", e);
        }
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(takePicture, 1);
    }

    private Uri getUri(String name, String extension) throws IOException {
        Date date = new Date();
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String path = storageDir.getAbsolutePath() + "/" + name;
        File file = new File(path);
        return Uri.fromFile(file);
    }

    private Bitmap getImageFromFile() {
        File imgFile = new File(imageUri.getPath());
        Bitmap bitmap = null;

        if(imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getImageFromFile() == null) {
            return;
        }

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (recognizer.isOperational()) {
            SparseArray<TextBlock> arr = recognizer.detect(new Frame.Builder()
                    .setBitmap(getImageFromFile())
                    .setRotation(ROTATION_90)
                    .build());

            String text = "";
            for(int i = 0; i < arr.size(); i++) {
                TextBlock block = arr.valueAt(i);
                text = text.concat(block.getValue());
            }

            textView.setText(text);
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    TextView view = (TextView)v;
                    int offset = view.getOffsetForPosition(event.getX(), event.getY());
                    CharSequence text = view.getText();
                    if (offset >= text.length()) {
                        return false;
                    }

                    List<Character> breakChars = Arrays.asList(' ', '\n');

                    int endIndex   = offset;
                    int startIndex = offset;
                    for (int i = offset; i < text.length() && !breakChars.contains(text.charAt(i)); i++) {
                        endIndex++;
                    }
                    for (int i = offset; i > 1 && !breakChars.contains(text.charAt(i)); i--) {
                        startIndex--;
                    }

                    lookup.setText(text.subSequence(startIndex, endIndex));

                    return false;
                }
            });
        }
    }
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id", Key.APP_ID);
                urlConnection.setRequestProperty("app_key", Key.APP_KEY);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                urlConnection.getInputStream().close();

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> response = mapper.readValue(result, new TypeReference<Map<String, Object>>() {
                });

                Log.d("Full JSON", response.toString());

                final String TAG = "MAPPING";
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                Log.d(TAG, results.toString());
                Map<String, Object> firstResult = results.get(0);
                Log.d(TAG, firstResult.toString());
                List<Map<String, Object>> lexicalEntries = (List<Map<String, Object>>) firstResult.get("lexicalEntries");
                Log.d(TAG, lexicalEntries.toString());
                Map<String, Object> firstLexicalEntry = lexicalEntries.get(0);
                Log.d(TAG, firstLexicalEntry.toString());
                List<Map<String, Object>> entries = (List<Map<String, Object>>) firstLexicalEntry.get("entries");
                Log.d(TAG, entries.toString());
                Map<String, Object> firstEntry = entries.get(0);
                Log.d(TAG, firstEntry.toString());
                List<Map<String, Object>> senses = (List<Map<String, Object>>) firstEntry.get("senses");
                Log.d(TAG, senses.toString());
                Map<String, Object> firstSense = senses.get(0);
                Log.d(TAG, firstSense.toString());
                List<String> definitions = (List<String>) firstSense.get("definitions");
                Log.d(TAG, definitions.toString());

                result = "";
                for (String definition : definitions) {
                    result += (definition + "\n");
                }
            }
            catch (IOException | NullPointerException | IndexOutOfBoundsException e) {
                Log.i("MainActivity", "Unable to find word", e);
                result = "No definition found.";
            }

            MainActivity.this.definition.setText(result);
        }

    }
}

