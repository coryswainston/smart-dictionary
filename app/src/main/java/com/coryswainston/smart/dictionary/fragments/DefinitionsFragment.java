package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.helpers.ParsingException;
import com.coryswainston.smart.dictionary.helpers.ParsingHelper;
import com.coryswainston.smart.dictionary.services.DictionaryLookupService;


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
    private String[] words;
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

        definitionsFragment.words = new String[] {word};
        definitionsFragment.selectedIndex = 0;
        definitionsFragment.selectedLanguage = selectedLanguage;

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
        definitionView.setVisibility(View.INVISIBLE);
        definitionView.setMovementMethod(new ScrollingMovementMethod());
        spinner = v.findViewById(R.id.definition_progress_gif);

        sharedPreferences = getContext().getSharedPreferences("lexiglass", 0);

        titleView = v.findViewById(R.id.definition_word_heading);
        titleView.setText(title);
        titleView.setEnabled(false);

        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        wordListView = v.findViewById(R.id.definitions_word_recycler);
        wordListView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(words);
        wordListView.setAdapter(recyclerAdapter);

        getDefinitionFromCacheOrService(words[selectedIndex]);

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
        // nothing for now
    }

    public void toggleWordEdit(View v) {
        Button b = (Button)v;

        if (titleView.isEnabled()) {
            titleView.setEnabled(false);

            b.setText("edit");
            b.setBackground(getResources().getDrawable(R.drawable.rounded_button));

            String oldTitle = title;
            title = titleView.getText().toString();
            if (!title.equals(oldTitle)) {
                spinner.setVisibility(View.VISIBLE);
                definitionView.setVisibility(View.INVISIBLE);
                words[selectedIndex] = title;
                recyclerAdapter.notifyDataSetChanged();
                getDefinitionFromCacheOrService(title);
            }
        } else {
            b.setText("save");
            b.setBackground(getResources().getDrawable(R.drawable.rounded_button_green));

            titleView.setEnabled(true);
            titleView.requestFocus();
        }
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
}
