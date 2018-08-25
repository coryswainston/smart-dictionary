package com.coryswainston.smart.dictionary.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment;
import com.coryswainston.smart.dictionary.fragments.SettingsFragment;
import com.coryswainston.smart.dictionary.helpers.fragment.FragmentHelper;
import com.coryswainston.smart.dictionary.services.DetectorProcessor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.coryswainston.smart.dictionary.helpers.fragment.FragmentHelper.TAG_DEFINITIONS_FRAGMENT;
import static com.coryswainston.smart.dictionary.helpers.fragment.FragmentHelper.TAG_SETTINGS_FRAGMENT;
import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_EN;
import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

public class MainActivity
        extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener,
        DefinitionsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private boolean surfaceAvailable;

    private Button captureButton;
    private Button defineButton;
    private View loadingGif;

    private SettingsFragment settingsFragment;
    private DefinitionsFragment definitionsFragment;
    private FragmentHelper fragmentHelper;

    private String selectedLanguage;

    private String detectedText;
    private List<Text> blocks;
    private String selectedWord;
    private MotionEvent activeEvent;
    private float ratio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        fragmentHelper = new FragmentHelper(this);
        captureButton = findViewById(R.id.capture_button);
        defineButton = findViewById(R.id.define_button);
        surfaceView = findViewById(R.id.surfaceView);
        loadingGif = findViewById(R.id.loading_gif);
        loadingGif.setVisibility(View.GONE);

        surfaceAvailable = false;
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        if (!checkForPermissions(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) {
            Toast.makeText(this, "Unable to obtain permissions.", Toast.LENGTH_SHORT).show();
        }

        selectedLanguage = LANGUAGE_EN;
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

            View.OnClickListener captureText = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loadingGif.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(MainActivity.this, DefineActivity.class);
                    intent.putExtra("detections", detectedText);
                    startActivity(intent);
                }
            };
            captureButton.setOnClickListener(captureText);

            surfaceView.setOnTouchListener(new View.OnTouchListener() {
                private boolean buzzed = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            if (eventTouchesDefineButton(event)) {
                                defineButton.setBackground(getResources().getDrawable(R.drawable.rounded_view_grey));
                                if (!buzzed) {
                                    defineButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                                    buzzed = true;
                                }
                                return true;
                            } else {
                                defineButton.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                buzzed = false;
                            }

                            activeEvent = event;

                            float screenX = event.getX();
                            float screenY = event.getY();

                            float realX = screenX * ratio;
                            float realY = screenY * ratio;

                            for(Text block : blocks) {
                                if (block.getBoundingBox().contains((int) realX, (int) realY)) {
                                    String tempWord = selectedWord;
                                    selectedWord = trimText(block);
                                    if (tempWord == null || !tempWord.equals(selectedWord)) {
                                        bringUpDefineButton(event);
                                    }
                                    break;
                                }
                            }
                            return true;
                        case MotionEvent.ACTION_UP:
                            defineButton.animate().scaleX(0).scaleY(0).setDuration(300);
                            if (eventTouchesDefineButton(event)) {
                                defineButton.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                buzzed = false;
                                defineButton.performClick();
                            } else {
                                activeEvent = null;
                                selectedWord = null;
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            });

        }

        private void bringUpDefineButton(MotionEvent event) {
            defineButton.setVisibility(View.VISIBLE);
            defineButton.setTransformationMethod(null);
            defineButton.setScaleX(0);
            defineButton.setScaleY(0);
            defineButton.animate().scaleX(1).scaleY(1).setDuration(300);
            defineButton.setText("Define \"" + selectedWord + "\"");
            float x = event.getX() + surfaceView.getLeft() - (float)defineButton.getWidth() / 2f;
            if (x < 0) {
                x = 0;
            }
            if (x > surfaceView.getRight() - defineButton.getWidth()) {
                x = surfaceView.getRight() - defineButton.getWidth();
            }
            defineButton.setX(x);
            float y = event.getY() + surfaceView.getTop() - (float)defineButton.getHeight() - 70;
            defineButton.setY(y);
        }

        private boolean eventTouchesDefineButton(MotionEvent event) {
            if (defineButton.getVisibility() == View.VISIBLE && defineButton.getScaleX() == 1) {
                int[] coords = new int[2];
                defineButton.getLocationInWindow(coords);
                Rect buttonPos = new Rect(coords[0],
                        coords[1],
                        coords[0] + defineButton.getWidth(),
                        coords[1] + defineButton.getHeight());
                if (buttonPos.contains((int) event.getX() + surfaceView.getLeft(),
                        (int) event.getY() + surfaceView.getTop())) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            surfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    public void onDefineButtonClick(View v) {
        if (fragmentHelper.fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT)) {
            definitionsFragment.addWord(selectedWord, selectedLanguage);
        } else {
            definitionsFragment = fragmentHelper.addDefinitionsFragment(selectedWord, selectedLanguage);
        }
        activeEvent = null;
        selectedWord = null;
    }

    private String trimText(Text block) {
        if (block == null || activeEvent == null) {
            return null;
        }
        String fullText = block.getValue();
        int width = block.getBoundingBox().width();
        int distanceToTouch = (int) (activeEvent.getX() * ratio) - block.getBoundingBox().left;
        float howFarInAreWe = distanceToTouch / width;
        int textLength = fullText.length();
        int approximateIndex = (int)(textLength * howFarInAreWe);
        if (!Character.isLetter(fullText.charAt(approximateIndex))) {
            for (int i = 1;
                 approximateIndex - i >= 0 || approximateIndex + i < fullText.length();
                 i++) {
                if (approximateIndex - i >= 0 && Character.isLetter(fullText.charAt(approximateIndex - i))) {
                    approximateIndex = approximateIndex - i;
                    break;
                }
                if (approximateIndex + i < fullText.length() && Character.isLetter(fullText.charAt(approximateIndex + i))) {
                    approximateIndex = approximateIndex + i;
                    break;
                }
            }
        }
        int startIndex = approximateIndex;
        int endIndex = approximateIndex;
        for (int i = approximateIndex; i < textLength && Character.isLetter(fullText.charAt(i)); i++) {
            endIndex++;
        }
        for (int i = approximateIndex; i > 0 && Character.isLetter(fullText.charAt(i - 1)); i--) {
            startIndex--;
        }

        return fullText.substring(startIndex, endIndex).toLowerCase();
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
        recognizer.setProcessor(new DetectorProcessor().withCallback(new DetectorProcessor.Callback() {
            @Override
            public void callback(List<Text> blocks, String detectedText) {
                MainActivity.this.detectedText = detectedText;
                MainActivity.this.blocks = blocks;
                if (activeEvent != null) {
                    surfaceView.onTouchEvent(activeEvent);
                }
            }
        }));

        if (!recognizer.isOperational()) {
            Log.w(TAG, "not operational");
        }

        cameraSource = new CameraSource.Builder(this, recognizer)
                .setFacing(CAMERA_FACING_BACK)
                .setRequestedFps(0.1f)
                .setRequestedPreviewSize(1200, 1200) // TODO most cameras won't have square ratios. Will need to find a hack
                .setAutoFocusEnabled(true)
                .build();

        startCameraSource();

        float cameraHeight = cameraSource.getPreviewSize().getHeight();
        float previewHeight = surfaceView.getHeight();

        Log.d(TAG, "camera height: " + cameraHeight + ", preview height: " + previewHeight);
        ratio = cameraHeight / previewHeight;
        Log.d(TAG, "ratio is: " + ratio);
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

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        loadingGif.setVisibility(View.GONE);

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

    /**
     * Called when the user hits the exit button in the corner.
     */
    @Override
    public void onDefinitionFragmentExit(View v) {
        if (definitionsFragment.onExit()) {
            fragmentHelper.removeDefinitionsFragment();
        }
    }

    /**
     * Called when the user hits the settings button.
     */
    public void onSettingsClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (fragmentHelper.fragmentIsPresent(TAG_SETTINGS_FRAGMENT)) {
            fragmentHelper.removeSettingsFragment();
        } else {
            settingsFragment = fragmentHelper.addSettingsFragment(selectedLanguage);
        }
    }

    /**
     * Called when the user hits the 'ok' button in the settings fragment.
     */
    @Override
    public void onSettingsOk(View v) {
        selectedLanguage = settingsFragment.onSettingsOk();
        fragmentHelper.removeSettingsFragment();
    }

    @Override
    public void toggleWordEdit(View v) {
        definitionsFragment.toggleWordEdit(v);
    }

    @Override
    public void onTabClick(View v) {
        definitionsFragment.onTabClick(v);
    }

    @Override
    public void onGoogleSearch(View v) {
        definitionsFragment.onGoogleSearch();
    }

    @Override
    public void onWikipediaSearch(View v) {
        definitionsFragment.onWikipediaSearch();
    }

    /**
     * Called when the user hits the 'cancel' button in the settings fragment.
     */
    public void onSettingsCancel(View v) {
        fragmentHelper.removeSettingsFragment();
    }

    @Override
    public void onWordsBackOrForward(View v) {
        definitionsFragment.onWordsBackOrForward(v);
    }

    @Override
    public void onBackPressed() {
        if (fragmentHelper.fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT)) {
            fragmentHelper.removeDefinitionsFragment();
        } else if (fragmentHelper.fragmentIsPresent(TAG_SETTINGS_FRAGMENT)) {
            fragmentHelper.removeSettingsFragment();
        } else {
            super.onBackPressed();
        }
    }
}

