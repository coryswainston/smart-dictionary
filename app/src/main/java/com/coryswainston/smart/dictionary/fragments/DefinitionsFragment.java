package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.helpers.ParsingException;
import com.coryswainston.smart.dictionary.helpers.ParsingHelper;
import com.coryswainston.smart.dictionary.services.DictionaryLookupService;

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
        titleView.setText(title);
        titleView.setEnabled(false);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        wordListView = v.findViewById(R.id.definitions_word_recycler);
        wordListView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(words);
        wordListView.setAdapter(recyclerAdapter);

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
                spinner.setVisibility(View.VISIBLE);
                definitionView.setVisibility(View.INVISIBLE);
                words.set(selectedIndex, title);
                recyclerAdapter.notifyDataSetChanged();
                getDefinitionFromCacheOrService(title);
            }
        } else {
            b.setText("save");
            b.setBackground(getResources().getDrawable(R.drawable.rounded_button_green));

            titleView.setEnabled(true);
            titleView.requestFocus();
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

    private void getDefinitionFromCacheOrService(String word) {
        DictionaryLookupService.Callback dictionaryCallback = new DictionaryLookupService.Callback() {
            @Override
            public void callback(String word, String result) {
                SpannableStringBuilder definitionsList;
                try {
                    definitionsList = ParsingHelper.parseDefinitionsFromJson(result);
                } catch (ParsingException e) {
                    definitionsList = new SpannableStringBuilder("No definition found.");
                }

                sharedPreferences.edit().putString(getKey(word), result).apply();

                definitionView.setText(definitionsList);
                definitionView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
            }
        };

        definitionView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);

        String cachedDefinition = sharedPreferences.getString(getKey(word), null);
        if (cachedDefinition != null) {
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
        for (int i = 0; i < words.size(); i++) {
            Log.d(TAG, "looking for existing views...");
            TextView tv = (TextView) layoutManager.findViewByPosition(i);
            if (tv != null) {
                Log.d(TAG, "View text is " + tv.getText());
                tv.setBackgroundColor(Color.WHITE);
            }
        }
        Log.d(TAG, "looking for new view...");
        TextView tv = (TextView) layoutManager.findViewByPosition(index);
        tv.setBackground(getContext().getResources().getDrawable(R.drawable.underline));

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
}
