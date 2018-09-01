package com.coryswainston.smart.dictionary.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment;
import com.coryswainston.smart.dictionary.listeners.WordGrabber;
import com.coryswainston.smart.dictionary.util.Settings;

public class DefineActivity extends AppCompatActivity
        implements DefinitionsFragment.OnFragmentInteractionListener {

    private static final String TAG = "DefineActivity";

    private TextView detectedWords;
    private DefinitionsFragment definitionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define);

        detectedWords = findViewById(R.id.detect_view);
        detectedWords.setOnTouchListener(new WordGrabber(new WordGrabber.Callback() {
            @Override
            public void callback(String text) {
                if (definitionsFragment == null) {
                    definitionsFragment = DefinitionsFragment.newInstance(text);
                } else {
                    definitionsFragment.addWord(text);
                }
                definitionsFragment.show(DefineActivity.this);
            }
        }));

        detectedWords.setText(getIntent().getStringExtra("detections"));
        detectedWords.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * Called when the user hits the back button.
     */
    public void onBack(View v) {
        onBackPressed();
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
}
