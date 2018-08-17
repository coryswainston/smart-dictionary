package com.coryswainston.smart.dictionary.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.services.DictionaryLookupService;
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment;
import com.coryswainston.smart.dictionary.helpers.ParsingException;
import com.coryswainston.smart.dictionary.helpers.ParsingHelper;
import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.fragments.SettingsFragment;
import com.coryswainston.smart.dictionary.listeners.WordGrabber;

import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_EN;
import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_ES;

public class DefineActivity extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener,
        DefinitionsFragment.OnFragmentInteractionListener {

    private static final String TAG = "DefineActivity";

    private static final String TAG_DEFINITIONS_FRAGMENT = "definitions";
    private static final String TAG_SETTINGS_FRAGMENT = "settings";

    private static final int FADE_IN_OUT_TIME = 100;

    private TextView detectedWords;
    private SettingsFragment settingsFragment;
    private DefinitionsFragment definitionsFragment;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPreferences;

    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define);

        selectedLanguage = LANGUAGE_EN;
        fragmentManager = getSupportFragmentManager();
        sharedPreferences = getSharedPreferences("lexiglass", 0);

        final DictionaryLookupService.Callback dictionaryCallback = (new DictionaryLookupService.Callback() {
            @Override
            public void callback(String word, String result) {
                SpannableStringBuilder definitionsList;
                try {
                    definitionsList = ParsingHelper.parseDefinitionsFromJson(result);
                } catch (ParsingException e) {
                    Log.i(TAG, "Unable to find word", e);
                    definitionsList = new SpannableStringBuilder("No definition found.");
                }

                sharedPreferences.edit().putString(word, result).apply();
                definitionsFragment.setDefinitions(definitionsList);
            }
        });

        detectedWords = findViewById(R.id.detect_view);
        detectedWords.setMovementMethod(new ScrollingMovementMethod());
        detectedWords.setOnTouchListener(new WordGrabber(new WordGrabber.Callback() {
            @Override
            public void callback(String text) {
                if (fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT)) {
                    return;
                }
                addDefinitionsFragment(text);
                String cachedDefinition = sharedPreferences.getString(text, null);
                if (cachedDefinition != null) {
                    Log.d(TAG, "avoiding API call");
                    dictionaryCallback.callback(text, cachedDefinition);
                } else {
                    Log.d(TAG, "making API call");
                    new DictionaryLookupService()
                            .withLanguage(selectedLanguage)
                            .withCallback(dictionaryCallback)
                            .execute(text);
                }
            }
        }));

        detectedWords.setText(getIntent().getStringExtra("detections"));
    }

    private void addDefinitionsFragment(String word) {
        removeSettingsFragment();

        if (!fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT)) {
            definitionsFragment = DefinitionsFragment.newInstance(word);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .add(R.id.define_container, definitionsFragment, TAG_DEFINITIONS_FRAGMENT)
                    .commit();
        }

        findViewById(R.id.define_container).setClickable(true);
    }

    private void removeDefinitionsFragment() {
        if (fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT) && definitionsFragment != null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .remove(definitionsFragment)
                    .commit();
        }

        findViewById(R.id.define_container).setClickable(false);
    }


    private void addSettingsFragment() {
        removeDefinitionsFragment();

        if (!fragmentIsPresent(TAG_SETTINGS_FRAGMENT)) {
            settingsFragment = SettingsFragment.newInstance(selectedLanguage);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_alpha_in, R.anim.fade_alpha_out)
                    .add(R.id.settings_container, settingsFragment, TAG_SETTINGS_FRAGMENT)
                    .commit();

            View wrapper = findViewById(R.id.define_wrapper);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(FADE_IN_OUT_TIME);
            wrapper.startAnimation(alphaAnimation);
        }
    }

    private void removeSettingsFragment() {
        if (fragmentIsPresent(TAG_SETTINGS_FRAGMENT) && settingsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_alpha_in, R.anim.fade_alpha_out)
                    .remove(settingsFragment)
                    .commit();

            View wrapper = findViewById(R.id.define_wrapper);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(FADE_IN_OUT_TIME);
            wrapper.startAnimation(alphaAnimation);
        }
    }

    private boolean fragmentIsPresent(String tag) {
        return fragmentManager.findFragmentByTag(tag) != null;
    }

    /**
     * Called when the user hits the back button.
     */
    public void onBack(View v) {
        Log.d(TAG, "onBack called");
        onBackPressed();
    }

    /**
     * Called when the user hits the exit button in the corner.
     */
    public void onDefinitionFragmentExit(View v) {
        removeDefinitionsFragment();
    }

    /**
     * Called when the user hits the settings button.
     */
    public void onSettingsClick(View v) {
        if (fragmentIsPresent(TAG_SETTINGS_FRAGMENT)) {
            removeSettingsFragment();
        } else {
            addSettingsFragment();
        }
    }

    /**
     * Called when the user hits the 'ok' button in the settings fragment.
     */
    public void onSettingsOk(View v) {
        if (settingsFragment.getView() != null) {
            RadioGroup languages = findViewById(R.id.radioGroup);

            switch (languages.getCheckedRadioButtonId()) {
                case R.id.english_radio:
                    selectedLanguage = LANGUAGE_EN;
                    break;
                case R.id.spanish_radio:
                    selectedLanguage = LANGUAGE_ES;
                    break;
            }
        }

        removeSettingsFragment();
    }

    public void toggleWordEdit(View v) {
        Button b = (Button)v;
        b.setText("Save");

        definitionsFragment.toggleWordEdit();
    }

    /**
     * Called when the user hits the 'cancel' button in the settings fragment.
     */
    public void onSettingsCancel(View v) {
        removeSettingsFragment();
    }
}
