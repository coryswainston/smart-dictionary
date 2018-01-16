package com.coryswainston.smart.dictionary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.DEFINITIONS;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.ENTRIES;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.LEXICAL_CATEGORY;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.LEXICAL_ENTRIES;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.RESULTS;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.SENSES;
import static com.google.android.gms.vision.Frame.ROTATION_90;

public class MainActivity extends AppCompatActivity {

    private TextView detectedTextView;
    private EditText searchBar;
    private TextView definitionView;
    private ProgressBar loadingGif;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        detectedTextView = findViewById(R.id.textView);
        searchBar = findViewById(R.id.lookup);
        definitionView = findViewById(R.id.definition);
        loadingGif = findViewById(R.id.loadingGif);
        Button searchButton = findViewById(R.id.search);

        ScrollingMovementMethod movementMethod = new ScrollingMovementMethod();
        detectedTextView.setMovementMethod(movementMethod);
        definitionView.setMovementMethod(movementMethod);

        detectedTextView.setOnTouchListener(new WordGrabber(searchBar));

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                definitionView.setText("");
                loadingGif.setVisibility(View.VISIBLE);

                Editable s = searchBar.getText();

                AsyncDictionaryLookup lookupTask = new AsyncDictionaryLookup();
                lookupTask.setListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(String result) {
                        SpannableStringBuilder definitionsList;
                        try {
                            definitionsList = parseDefinitionsFromJson(result);
                        } catch (IOException | NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
                            Log.i("MainActivity", "Unable to find word", e);
                            definitionsList = new SpannableStringBuilder("No definition found.");
                        }

                        loadingGif.setVisibility(View.GONE);
                        definitionView.setText(definitionsList);
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

    private SpannableStringBuilder parseDefinitionsFromJson(String s) throws IOException,
            NullPointerException,
            IndexOutOfBoundsException,
            ClassCastException {

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

        ParsableJson<Object> result = new ParsableJson<>(s)
                .getList(RESULTS)
                .getObject(0);

        String word = result.getAsMap(String.class, String.class).get("word");
        stringBuilder.append(word);
        stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append("\n\n");

        ParsableJson<List<Object>> lexicalEntries = result.getList(LEXICAL_ENTRIES);

        for (ParsableJson<Object> lexicalEntry : lexicalEntries) {
            Map<String, String> lexicalEntryMap = lexicalEntry.getAsMap(String.class, String.class);
            String lexicalCategory = lexicalEntryMap.get(LEXICAL_CATEGORY).toLowerCase();
            stringBuilder.append(lexicalCategory);
            stringBuilder.setSpan(new StyleSpan(Typeface.ITALIC), stringBuilder.length() - lexicalCategory.length(),
                    stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append("\n");

            ParsableJson<List<Object>> senses = lexicalEntry.getList(ENTRIES)
                    .getObject(0)
                    .getList(SENSES);

            for (ParsableJson<Object> sense : senses) {
                List<String> definitions = sense.getList(DEFINITIONS).getAsList(String.class);
                String definition = definitions.get(0);

                int definitionNumber = senses.get().indexOf(sense.get()) + 1;
                stringBuilder.append(String.format("%s. %s%n", definitionNumber, definition));
            }
            stringBuilder.append('\n');
        }

        return stringBuilder;
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

        for (int result : results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Unable to obtain permissions.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        openCamera();
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            String path = storageDir.getAbsolutePath() + "/smart-dictionary-temp.jpg";
            File file = new File(path);
            imageUri = Uri.fromFile(file);
        }
        catch (Exception e) {
            Log.e("MainActivity", "unable to get uri", e);
            Toast.makeText(this, "Error processing photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(takePicture, 1);
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

            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < arr.size(); i++) {
                TextBlock block = arr.valueAt(i);
                List<? extends Text> lines = block.getComponents();
                for (Text line : lines) {
                    stringBuilder.append(line.getValue());
                    stringBuilder.append(' ');
                }
                stringBuilder.append('\n');
            }

            detectedTextView.setText(stringBuilder.toString());
        }
    }

    public void onNewPicture(View v) {
        openCamera();
    }
}

