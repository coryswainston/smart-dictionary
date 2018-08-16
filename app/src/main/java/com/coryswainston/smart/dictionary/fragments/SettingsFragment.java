package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.coryswainston.smart.dictionary.R;


/**
 * A fragment to update the language settings for the app.
 */
public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener interactionListener;

    private String language;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DefinitionsFragment.
     */
    public static SettingsFragment newInstance(String language) {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.language = language;
        return settingsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        switch (language) {
            case "en":
                radioGroup.check(R.id.english_radio);
                break;
            case "es":
                radioGroup.check(R.id.spanish_radio);
                break;
        }
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
     * Do something
     */
    public interface OnFragmentInteractionListener {
        // do nothin
    }
}
