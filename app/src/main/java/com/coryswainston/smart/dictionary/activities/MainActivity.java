package com.coryswainston.smart.dictionary.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment;
import com.coryswainston.smart.dictionary.services.DetectorProcessor;
import com.coryswainston.smart.dictionary.util.CameraUtils;
import com.coryswainston.smart.dictionary.util.Settings;
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

import static com.coryswainston.smart.dictionary.util.CameraUtils.selectSizePair;
import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

public class MainActivity
        extends AppCompatActivity
        implements DefinitionsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private boolean requested = false;

    private static final int DESIRED_WIDTH = 1200;
    private static final int DESIRED_HEIGHT = 1200;

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private boolean surfaceAvailable;

    private Button captureButton;
    private Button defineButton;
    private View loadingGif;
    private ConstraintLayout wrapper;

    private DefinitionsFragment definitionsFragment;

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

        wrapper = findViewById(R.id.wrapper);
        captureButton = findViewById(R.id.capture_button);
        defineButton = findViewById(R.id.define_button);
        defineButton.setScaleX(0);
        defineButton.setScaleY(0);
        surfaceView = findViewById(R.id.surfaceView);
        loadingGif = findViewById(R.id.loading_gif);
        loadingGif.setVisibility(View.GONE);

        surfaceAvailable = false;
        if (checkForPermissions(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) {
            surfaceView.getHolder().addCallback(new SurfaceCallback());
        }
    }

    private void adjustPreviewSize() {
        // determine what the camera size will be
        try {
            Camera testInstance = Camera.open();
            CameraUtils.SizePair sizePair = CameraUtils.selectSizePair(testInstance, DESIRED_WIDTH, DESIRED_HEIGHT);

            int adjustedWidth = sizePair.previewSize().getWidth();
            int adjustedHeight = sizePair.previewSize().getHeight();

            testInstance.release();

            // manually change surfaceView to fit camera size
            surfaceView.setLayoutParams(new ConstraintLayout.LayoutParams(adjustedWidth, adjustedHeight));

            int height = surfaceView.getHeight();
            int width = surfaceView.getWidth();

            ratio = adjustedHeight / height;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(wrapper);

            constraintSet.connect(R.id.surfaceView,ConstraintSet.LEFT,R.id.wrapper,ConstraintSet.LEFT,0);
//        constraintSet.connect(R.id.surfaceView,ConstraintSet.RIGHT,R.id.wrapper,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.surfaceView,ConstraintSet.TOP,R.id.instruction_main,ConstraintSet.BOTTOM,24);
//        constraintSet.connect(R.id.surfaceView,ConstraintSet.BOTTOM,R.id.capture_button,ConstraintSet.TOP,24);
            constraintSet.applyTo(wrapper);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            surfaceAvailable = true;
            adjustPreviewSize();
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
                                defineButton.setBackground(getResources().getDrawable(R.drawable.define_button_pressed_bg));
                                if (!buzzed) {
                                    defineButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                                    buzzed = true;
                                }
                                return true;
                            } else {
                                defineButton.setBackground(getResources().getDrawable(R.drawable.define_button_bg));
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
                                defineButton.setBackground(getResources().getDrawable(R.drawable.define_button_bg));
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
            defineButton.setText(selectedWord);
            float x = event.getX() + surfaceView.getLeft() - (float)defineButton.getWidth() / 2f;
            if (x < 0) {
                x = 0;
            }
            if (x > surfaceView.getRight() - defineButton.getWidth()) {
                x = surfaceView.getRight() - defineButton.getWidth();
            }
            float y = event.getY() + surfaceView.getTop() - (float)defineButton.getHeight() - 80;

            if (defineButton.getScaleX() == 0) {
                defineButton.setX(x);
                defineButton.setY(y);
                defineButton.setTransformationMethod(null);
                defineButton.animate().scaleX(1).scaleY(1).setDuration(300);
            } else {
                defineButton.animate().translationX(x);
                defineButton.animate().translationY(y);
            }
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
        if (definitionsFragment == null) {
            definitionsFragment = DefinitionsFragment.newInstance(selectedWord);
        } else {
            definitionsFragment.addWord(selectedWord);
        }
        definitionsFragment.show(this);
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
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.isEmpty()) {
            return true;
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 0);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] results) {
        if (requestCode != 0) {
            return;
        }

        for (int i = 0; i < results.length; i++) {
            if (results[i] != PackageManager.PERMISSION_GRANTED && permissions[i] != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Permissions needed");
                alertDialog.setMessage("This app requires camera permissions to function properly.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.INTERNET
                                        },
                                        0);
                            }
                        });
                alertDialog.show();
                return;
            }
        }
        surfaceView.getHolder().addCallback(new SurfaceCallback());
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
                .setRequestedPreviewSize(DESIRED_WIDTH, DESIRED_HEIGHT)
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

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        loadingGif.setVisibility(View.GONE);

        if (cameraSource != null) {
            try {
                startCameraSource();
            }
            catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    /**
     * Called when the user hits the exit button in the corner.
     */
    @Override
    public void onDefinitionFragmentExit(View v) {
        if (definitionsFragment.onExit()) {
            definitionsFragment = null;
        }
    }

    /**
     * Called when the user hits the settings button.
     */
    public void onSettingsClick(View v) {
        Settings.showDialog(this);
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

    @Override
    public void onWordsBackOrForward(View v) {
        definitionsFragment.onWordsBackOrForward(v);
    }

    @Override
    public void onBackPressed() {
        if (definitionsFragment != null) {
            definitionsFragment.remove();
            definitionsFragment = null;
        } else {
            super.onBackPressed();
        }
    }
}

