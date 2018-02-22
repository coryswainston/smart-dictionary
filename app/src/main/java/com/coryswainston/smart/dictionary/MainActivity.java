package com.coryswainston.smart.dictionary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

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
import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

public class MainActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private boolean surfaceAvailable;
    private TextView detectionsView;
    private Map<String, Rect> detectionsMap;
    private String detectedText;

    private EditText searchBar;
    private TextView definitionView;
    private ProgressBar loadingGif;

    private Uri imageUri;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        surfaceView = findViewById(R.id.surfaceView);
        detectionsView = findViewById(R.id.detectionsView);
        searchBar = findViewById(R.id.lookup);
        detectionsView.setOnTouchListener(new WordGrabber(searchBar));
        surfaceAvailable = false;
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        definitionView = findViewById(R.id.definition);
        loadingGif = findViewById(R.id.loadingGif);
        Button searchButton = findViewById(R.id.search);

        ScrollingMovementMethod movementMethod = new ScrollingMovementMethod();
        definitionView.setMovementMethod(movementMethod);
        detectionsView.setMovementMethod(movementMethod);

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

        if (!checkForPermissions(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) {
            Toast.makeText(this, "Unable to obtain permissions.", Toast.LENGTH_SHORT).show();;
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

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            surfaceAvailable = true;
            try {
                setUpCamera();
            }
            catch (SecurityException e) {
                Log.e("surfaceCallback", e.toString());
            }
            catch (IOException e) {
                Log.e("surfaceCallback", e.toString());
            }
            View.OnTouchListener surfaceViewListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    Log.d(TAG, "BOUNDS LOCATION: " + location[0] + "," + location[1]);

                    float x = event.getX() - location[0];
                    float y = event.getY() - location[1];

                    for (String word : detectionsMap.keySet()) {
                        Rect bounds = detectionsMap.get(word);
                        Log.d(TAG, "BOUNDS: " + bounds.toString() + "(" + word + "), EVENT: " + x + "," + y);
                        if (x >= bounds.left && x <= bounds.right && y >= bounds.bottom && y <= bounds.top) {
                            definitionView.setText(word);
                        }
                    }

                    return false;
                }
            };
            surfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    cameraSource.stop();
                    surfaceView.setVisibility(View.INVISIBLE);
                    detectionsView.setVisibility(View.VISIBLE);
                    detectionsView.setText(detectedText);
                    if (definitionView.getText().toString().equals(getResources().getString(R.string.picture_instructions))) {
                        definitionView.setText(getResources().getString(R.string.word_instructions));
                    } else {
                        Log.d(TAG, definitionView.getText().toString() + ", " + getResources().getString(R.string.picture_instructions));
                    }
                    return false;
                }
            });
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            surfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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

        for (int result : results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Unable to obtain permissions.", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    private void setUpCamera() throws IOException, SecurityException {

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        recognizer.setProcessor(new DetectorProcessor().withListener(new OnProcessedListener() {
            @Override
            public void onProcessed(Map<String, Rect> result, String text) {
                detectionsMap = result;
                detectedText = text;
            }
        }));

        if (!recognizer.isOperational()) {
            Log.w("main", "not operational");

        }

        int x = surfaceView.getWidth();
        int y = surfaceView.getHeight();

        cameraSource = new CameraSource.Builder(this, recognizer)
                .setFacing(CAMERA_FACING_BACK)
                .setRequestedFps(0.1f)
                .setRequestedPreviewSize(y, x)
                .setAutoFocusEnabled(true)
                .build();



        startCameraSource();
    }

    private void startCameraSource() throws IOException, SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Play Services Unavailable", Toast.LENGTH_SHORT).show();
        }

        if (surfaceAvailable) {
            Log.d(TAG, "surface was available");
            cameraSource.start(surfaceView.getHolder());
            Log.d(TAG, "started cameraSource");
        } else {
            Log.d(TAG, "surface wasn't available");
        }
    }

    public void onNewPicture(View v) {
        detectionsView.setVisibility(View.INVISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
        try {
            startCameraSource();
        }
        catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            startCameraSource();
        }
        catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }
}

