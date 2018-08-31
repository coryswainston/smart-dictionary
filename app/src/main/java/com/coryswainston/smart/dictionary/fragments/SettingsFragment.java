package com.coryswainston.smart.dictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RadioGroup;

import com.coryswainston.smart.dictionary.R;

import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_EN;
import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_ES;


/**
 * A fragment to update the language settings for the app.
 */
public class SettingsFragment extends Fragment {
    public static final String TAG = "settings";
    private static final int FADE_IN_OUT_TIME = 200;

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

    public interface OnFragmentInteractionListener {
        void onSettingsOk(View v);
    }

    public String onSettingsOk() {
        RadioGroup languages = getView().findViewById(R.id.radioGroup);

        String selectedLanguage = null;
        switch (languages.getCheckedRadioButtonId()) {
            case R.id.english_radio:
                selectedLanguage = LANGUAGE_EN;
                break;
            case R.id.spanish_radio:
                selectedLanguage = LANGUAGE_ES;
                break;
        }

        this.remove();
        return selectedLanguage;
    }

    public void show(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(TAG) == null) {
            DefinitionsFragment definitionsFragment = (DefinitionsFragment) fragmentManager.findFragmentByTag(DefinitionsFragment.TAG);
            if(definitionsFragment != null) {
                definitionsFragment.remove();
            }
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_alpha_in, R.anim.fade_alpha_out)
                    .add(R.id.settings_container, this, TAG)
                    .commit();

            View wrapper = activity.findViewById(R.id.wrapper);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(FADE_IN_OUT_TIME);
            wrapper.startAnimation(alphaAnimation);
            activity.findViewById(R.id.settings_container).setClickable(true);
        }
    }

    public void remove() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(TAG) != null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_alpha_in, R.anim.fade_alpha_out)
                    .remove(this)
                    .commit();

            View wrapper = activity.findViewById(R.id.wrapper);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(FADE_IN_OUT_TIME);
            wrapper.startAnimation(alphaAnimation);
            activity.findViewById(R.id.settings_container).setClickable(false);
        }
    }
}
