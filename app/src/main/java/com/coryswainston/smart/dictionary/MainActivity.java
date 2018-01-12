package com.coryswainston.smart.dictionary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.vision.Frame.ROTATION_90;

public class MainActivity extends AppCompatActivity {

    private TextView detectedTextView;
    private EditText searchBar;
    private TextView definitionView;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        detectedTextView = (TextView)findViewById(R.id.textView);
        searchBar = (EditText)findViewById(R.id.lookup);
        definitionView = (TextView)findViewById(R.id.definition);
        Button searchButton = (Button)findViewById(R.id.search);

        ScrollingMovementMethod movementMethod = new ScrollingMovementMethod();
        detectedTextView.setMovementMethod(movementMethod);
        definitionView.setMovementMethod(movementMethod);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Editable s = searchBar.getText();

                AsyncDictionaryLookup lookupTask = new AsyncDictionaryLookup();
                lookupTask.setListener(new OnCompleteListener() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onComplete(String result) {
                        String definitionsList = "";
                        try {
                            ParsableJson<Map<String, Object>> json = new ParsableJson<>(result);

                            List<Map<String, Object>> senses = json.getList("results")
                                    .getObject(0)
                                    .getList("lexicalEntries")
                                    .getObject(0)
                                    .getList("entries")
                                    .getObject(0)
                                    .getList("senses")
                                    .getJson();

                            for (Map<String, Object> sense : senses) {
                                ParsableJson senseObject = new ParsableJson(sense);
                                List<String> definitions = senseObject.getListOfObjects("definitions");
                                String definition = definitions.get(0);

                                int definitionNumber = senses.indexOf(sense) + 1;
                                definitionsList += String.format("%s. %s%n%n", definitionNumber, definition);
                            }
                        } catch (IOException | NullPointerException | IndexOutOfBoundsException e) {
                            Log.i("MainActivity", "Unable to find word", e);
                            definitionsList = "No definition found.";
                        }

                        MainActivity.this.definitionView.setText(definitionsList);
                    }
                });

                lookupTask.execute("https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" +
                        s.toString().toLowerCase());
            }
        });

        if (checkForPermissions(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) {
            openCamera();
        }
    }

    private boolean checkForPermissions(String ... permissions) {
        List<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions));

        for (Iterator<String> it = permissionsToRequest.iterator(); it.hasNext();) {
            if (ContextCompat.checkSelfPermission(this, it.next()) == PackageManager.PERMISSION_GRANTED) {
                it.remove();
            }
        }
        if (permissionsToRequest.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(permissions), 0);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] results) {
        if (requestCode != 0) {
            return;
        }
        if (results.length > 2
                && results[0] == PackageManager.PERMISSION_GRANTED
                && results[1] == PackageManager.PERMISSION_GRANTED
                && results[2] == PackageManager.PERMISSION_GRANTED) {
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

            detectedTextView.setText(text);
            detectedTextView.setOnTouchListener(new View.OnTouchListener() {
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

                    searchBar.setText(text.subSequence(startIndex, endIndex));

                    return false;
                }
            });
        }
    }
}

