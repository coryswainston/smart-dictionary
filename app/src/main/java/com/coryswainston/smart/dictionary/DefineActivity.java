package com.coryswainston.smart.dictionary;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.DEFINITIONS;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.ENTRIES;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.LEXICAL_CATEGORY;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.LEXICAL_ENTRIES;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.RESULTS;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.SENSES;

public class DefineActivity extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener,
                   DefineFragment.OnFragmentInteractionListener {

    private static final String TAG = "DefineActivity";

    private TextView detectView;
    private Fragment settingsFragment;
    private Fragment defineFragment;

    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define);

        final ImageButton settingsButton = findViewById(R.id.settings_button);
        final ImageButton backButton = findViewById(R.id.back_button);
        addTouchAreaToView(settingsButton);
        addTouchAreaToView(backButton);

        AsyncDictionaryLookup lookupTask = new AsyncDictionaryLookup();
        lookupTask.setLang("en");
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

                addDefineFragment(definitionsList.toString());
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
        });

        detectView = findViewById(R.id.detect_view);
        detectView.setOnTouchListener(new WordGrabber(lookupTask));
        ScrollingMovementMethod movementMethod = new ScrollingMovementMethod();
        detectView.setMovementMethod(movementMethod);

        detectView.setText(getIntent().getStringExtra("detections"));
    }

    private void addTouchAreaToView(final View v) {
        final View parent = (View) v.getParent();
        parent.post(new Runnable() {
            @Override
            public void run() {
                final Rect bounds = new Rect();
                v.getHitRect(bounds);
                bounds.top -= 100;
                bounds.left -= 100;
                bounds.right += 100;
                bounds.bottom += 100;
                parent.setTouchDelegate(new TouchDelegate(bounds, v));
            }
        });
    }

    public void onBack(View v) {
        Log.d(TAG, "onBack called");
        onBackPressed();
    }

    public void onDefineClose(View v) {
        removeDefineFragment();
    }

    private void addDefineFragment(String definitions) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        defineFragment = DefineFragment.newInstance(definitions);

        if (fragmentManager.findFragmentByTag("define") == null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .add(R.id.define_container, defineFragment, "define")
                    .commit();
        }
    }

    private void removeDefineFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment defFragInView = fragmentManager.findFragmentByTag("define");

        if (defFragInView != null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .remove(defFragInView)
                    .commit();
        }
    }


    public void onSettingsClick(View v) {
        if (fragmentIsPresent("settings")) {
            removeSettingsFragment();
        } else {
            addSettingsFragment();
        }
    }

    public void onSettingsOk(View v) {
        View fragmentView = settingsFragment.getView();
        if (fragmentView != null) {
            RadioGroup langs = findViewById(R.id.radioGroup);
            int selected = langs.getCheckedRadioButtonId();
            switch (selected) {
                case R.id.english_radio:
                    lang = "en";
                    break;
                case R.id.spanish_radio:
                    lang = "es";
                    break;
            }
        }

        removeSettingsFragment();
    }

    public void onSettingsCancel(View v) {
        removeSettingsFragment();
    }

    private void removeSettingsFragment() {
        if (fragmentIsPresent("settings") && settingsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(settingsFragment)
                    .commit();
        }
        View wrapper = findViewById(R.id.define_wrapper);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(500);
        wrapper.startAnimation(alphaAnimation);
    }

    private void addSettingsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (!fragmentIsPresent("settings")) {
            settingsFragment = new SettingsFragment();

            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.settings_container, settingsFragment, "settings")
                    .commit();
        }
        View wrapper = findViewById(R.id.define_wrapper);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(500);
        wrapper.startAnimation(alphaAnimation);
    }

    private boolean fragmentIsPresent(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag) != null;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
