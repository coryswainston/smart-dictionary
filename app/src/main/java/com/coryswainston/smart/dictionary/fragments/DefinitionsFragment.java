package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coryswainston.smart.dictionary.R;


/**
 * A fragment to hold definitions of words
 */
public class DefinitionsFragment extends Fragment {

    private OnFragmentInteractionListener interactionListener;

    private EditText titleView;
    private TextView definitionView;
    private String title;
    private String definitions;
    private ProgressBar spinner;

    public DefinitionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DefinitionsFragment.
     */
    public static DefinitionsFragment newInstance(String word) {
        DefinitionsFragment definitionsFragment = new DefinitionsFragment();
        definitionsFragment.title = word;
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
        if (definitions != null) {
            populateDefinitionView();
        }

        titleView = v.findViewById(R.id.definition_word_heading);
        titleView.setText(title);
        titleView.setEnabled(false);


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

    public void toggleWordEdit() {
        if (titleView.isEnabled()) {
            titleView.setEnabled(false);
        } else {
            titleView.setEnabled(true);
            titleView.requestFocus();
        }
    }

    private void populateDefinitionView() {
        definitionView.setText(definitions);
        definitionView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
        if (getView() != null) {
            populateDefinitionView();
        }
    }
}
