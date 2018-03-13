package com.coryswainston.smart.dictionary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

public class MainActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private Button captureButton;
    private boolean surfaceAvailable;
    private String detectedText;
    private View loadingGif;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        captureButton = findViewById(R.id.capture_button);
        surfaceView = findViewById(R.id.surfaceView);
        loadingGif = findViewById(R.id.loading_gif);

        loadingGif.setVisibility(View.GONE);

        surfaceAvailable = false;
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        if (!checkForPermissions(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) {
            Toast.makeText(this, "Unable to obtain permissions.", Toast.LENGTH_SHORT).show();;
        }
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
            surfaceView.setOnClickListener(captureText);
            captureButton.setOnClickListener(captureText);

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
                .setRequestedPreviewSize(1000, 1200)
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

