package com.coryswainston.smart.dictionary.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment;
import com.coryswainston.smart.dictionary.fragments.SettingsFragment;
import com.coryswainston.smart.dictionary.listeners.WordGrabber;

import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_EN;

public class DefineActivity extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener,
        DefinitionsFragment.OnFragmentInteractionListener {

    private static final String TAG = "DefineActivity";

    private TextView detectedWords;
    private DefinitionsFragment definitionsFragment;
    private SettingsFragment settingsFragment;

    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define);

        selectedLanguage = LANGUAGE_EN;

        detectedWords = findViewById(R.id.detect_view);
        detectedWords.setOnTouchListener(new WordGrabber(new WordGrabber.Callback() {
            @Override
            public void callback(String text) {
                if (definitionsFragment == null) {
                    definitionsFragment = DefinitionsFragment.newInstance(text, selectedLanguage);
                } else {
                    definitionsFragment.addWord(text, selectedLanguage);
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
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
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
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance(selectedLanguage);
            settingsFragment.show(this);
        } else {
            settingsFragment.remove();
            settingsFragment = null;
        }
    }

    /**
     * Called when the user hits the 'ok' button in the settings fragment.
     */
    @Override
    public void onSettingsOk(View v) {
        selectedLanguage = settingsFragment.onSettingsOk();
    }

    @Override
    public void toggleWordEdit(View v) {
        definitionsFragment.toggleWordEdit(v);
    }

    @Override
    public void onTabClick(View v) {
        Log.d(TAG, "In onTabClick");

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
        settingsFragment.remove();
        settingsFragment = null;
    }

    @Override
    public void onWordsBackOrForward(View v) {
        definitionsFragment.onWordsBackOrForward(v);
    }
}
