package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.helpers.ParsingException;
import com.coryswainston.smart.dictionary.helpers.ParsingHelper;
import com.coryswainston.smart.dictionary.services.DictionaryLookupService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to hold definitions of words
 */
public class DefinitionsFragment extends Fragment {

    private static final String TAG = "DefinitionsFragment";

    private OnFragmentInteractionListener interactionListener;
    private SharedPreferences sharedPreferences;

    private String selectedLanguage;
    private EditText titleView;
    private TextView definitionView;
    private String title;
    private ProgressBar spinner;
    private RecyclerView wordListView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private List<String> words;
    private int selectedIndex;

    private Button googleSearchButton;
    private Button wikipediaSearchButton;

    public DefinitionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DefinitionsFragment.
     */
    public static DefinitionsFragment newInstance(String word, String selectedLanguage) {
        DefinitionsFragment definitionsFragment = new DefinitionsFragment();

        definitionsFragment.title = word;
        definitionsFragment.words = new ArrayList<>();
        definitionsFragment.selectedLanguage = selectedLanguage;
        definitionsFragment.selectedIndex = -1;

        return definitionsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_definitions, container, false);
        definitionView = v.findViewById(R.id.definition_text);
        definitionView.setMovementMethod(new ScrollingMovementMethod());
        spinner = v.findViewById(R.id.definition_progress_gif);

        sharedPreferences = getContext().getSharedPreferences("lexiglass", 0);

        titleView = v.findViewById(R.id.definition_word_heading);
        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleView.setTextSize(32 - 8 * (s.length() / 10));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        });
        final Button editButton = v.findViewById(R.id.edit_button);
        titleView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View localTitleView, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            toggleWordEdit(editButton);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        titleView.setText(title);
        titleView.setEnabled(false);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        wordListView = v.findViewById(R.id.definitions_word_recycler);
        wordListView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(words);
        wordListView.setAdapter(recyclerAdapter);

        googleSearchButton = v.findViewById(R.id.definitions_google_search);
        wikipediaSearchButton = v.findViewById(R.id.definitions_wiki_search);

        addWord(title, selectedLanguage);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    /**
     * Handles interactions with activity.
     */
    public interface OnFragmentInteractionListener {
        // should call this.onTabClick(v)
        void onTabClick(View v);
        // should call this.onExit() and remove fragment when true is returned
        void onDefinitionFragmentExit(View v);
        // should call this.toggleWordEdit()
        void toggleWordEdit(View v);
        // should call this.onGoogleSearch()
        void onGoogleSearch(View v);
        // should call this.onWikipediaSearch()
        void onWikipediaSearch(View v);
    }

    public void onTabClick(View v) {
        Log.d(TAG, "In onTabClick");

        selectTab(layoutManager.getPosition(v));
    }

    public void toggleWordEdit(View v) {
        Button b = (Button) v;

        if (titleView.isEnabled()) {
            titleView.setEnabled(false);

            b.setText("edit");
            b.setBackground(getResources().getDrawable(R.drawable.rounded_button));

            String oldTitle = title;
            title = titleView.getText().toString();
            if (!title.equals(oldTitle)) {
                words.set(selectedIndex, title);
                recyclerAdapter.notifyDataSetChanged();
                getDefinitionFromCacheOrService(title);
            }
        } else {
            b.setText("save");
            b.setBackground(getResources().getDrawable(R.drawable.rounded_button_green));

            titleView.setEnabled(true);
            titleView.requestFocus();
            titleView.setSelection(title.length());
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }

        float scale = getResources().getDisplayMetrics().density;
        int paddingHorizontalInPixels = (int) (8 * scale + 0.5f);
        int paddingVerticalInPixels = (int) (2 * scale + 0.5f);
        b.setPadding(paddingHorizontalInPixels,
                paddingVerticalInPixels,
                paddingHorizontalInPixels,
                paddingVerticalInPixels);
    }

    private String getKey(String word) {
        return String.format("%s-%s", selectedLanguage, word);
    }

    private void fadeIn(View v) {
        v.setAlpha(0);
        v.setVisibility(View.VISIBLE);
        v.animate().alpha(1).setDuration(300);
    }

    private void getDefinitionFromCacheOrService(String word) {
        DictionaryLookupService.Callback dictionaryCallback = new DictionaryLookupService.Callback() {
            @Override
            public void callback(String word, String result) {
                SpannableStringBuilder definitionsList;
                try {
                    definitionsList = ParsingHelper.parseDefinitionsFromJson(result);
                } catch (ParsingException e) {
                    e.printStackTrace();
                    definitionsList = new SpannableStringBuilder("No definition found.");
                }

                if (definitionsList != null) {
                    sharedPreferences.edit().putString(getKey(word), result).apply();
                } else {
                    definitionsList = new SpannableStringBuilder("Check Internet connection");
                }

                definitionView.setText(definitionsList);
                fadeIn(definitionView);
                if (definitionsList.toString().equals("No definition found.")) {
                    fadeIn(googleSearchButton);
                    fadeIn(wikipediaSearchButton);
                }
                spinner.animate().alpha(0).setDuration(300);
                spinner.setVisibility(View.GONE);
            }
        };

        googleSearchButton.setVisibility(View.GONE);
        wikipediaSearchButton.setVisibility(View.GONE);
        definitionView.setVisibility(View.INVISIBLE);
        fadeIn(spinner);

        String cachedDefinition = sharedPreferences.getString(getKey(word), null);
        if (cachedDefinition != null && !word.equals("get")) {
            Log.d(TAG, "avoiding API call");
            dictionaryCallback.callback(word, cachedDefinition);
        } else {
            Log.d(TAG, "making API call");
            new DictionaryLookupService()
                    .withLanguage(selectedLanguage)
                    .withCallback(dictionaryCallback)
                    .execute(word);
        }
    }

    private void selectTab(int index) {
        if (index == selectedIndex) {
            return;
        }
        selectedIndex = index;
        title = words.get(index);
        titleView.setText(title);
        Resources res = getContext().getResources();
        for (int i = 0; i < words.size(); i++) {
            Log.d(TAG, "looking for existing views...");
            TextView tv = (TextView) layoutManager.findViewByPosition(i);
            if (tv != null) {
                Log.d(TAG, "View text is " + tv.getText());
                tv.setBackgroundColor(Color.WHITE);
                tv.setTextColor(res.getColor(R.color.colorDeselected));
            }
        }
        Log.d(TAG, "looking for new view...");
        TextView tv = (TextView) layoutManager.findViewByPosition(index);
        tv.setBackground(getContext().getResources().getDrawable(R.drawable.underline));
        tv.setTextColor(res.getColor(R.color.colorPrimary));

        getDefinitionFromCacheOrService(words.get(index));
    }

    public void addWord(String word, String selectedLanguage) {
        final int wordIndex;
        if (words.contains(word)) {
            wordIndex = words.indexOf(word);
        } else {
            this.selectedLanguage = selectedLanguage;
            words.add(word);
            recyclerAdapter.notifyDataSetChanged();
            wordIndex = words.size() - 1;
        }
        wordListView.scrollToPosition(wordIndex);

        wordListView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        selectTab(wordIndex);
                        wordListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    /**
     * Removes the current word when exit is clicked.
     *
     * @return true if the fragment is empty and should be removed.
     */
    public boolean onExit() {
        if(words.size() <= 1) {
            return true;
        }

        words.remove(selectedIndex);
        final int newIndex;
        recyclerAdapter.notifyDataSetChanged();
        if (selectedIndex >= words.size()) {
            newIndex = words.size() - 1;
        } else {
            newIndex = selectedIndex;
        }
        selectedIndex = -1;
        wordListView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        selectTab(newIndex);
                        wordListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        return false;
    }

    public void onGoogleSearch() {
        try {
            String escapedQuery = URLEncoder.encode(words.get(selectedIndex), "UTF-8");
            Uri uri = Uri.parse("https://www.google.com/#q=" + escapedQuery);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void onWikipediaSearch() {
        try {
            String escapedQuery = URLEncoder.encode(words.get(selectedIndex), "UTF-8");
            Uri uri = Uri.parse("https://wikipedia.org/w/index.php?search=" + escapedQuery);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        }
    }
}
