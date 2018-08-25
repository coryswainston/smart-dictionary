package com.coryswainston.smart.dictionary.helpers.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.coryswainston.smart.dictionary.R;
import com.coryswainston.smart.dictionary.fragments.DefinitionsFragment;
import com.coryswainston.smart.dictionary.fragments.SettingsFragment;

/**
 * For generic fragment code
 */

public class FragmentHelper {

    public static final String TAG_DEFINITIONS_FRAGMENT = "definitions";
    public static final String TAG_SETTINGS_FRAGMENT = "settings";

    private static final int FADE_IN_OUT_TIME = 200;

    private Context context;
    private FragmentManager fragmentManager;

    public FragmentHelper(Context context) {
        this.context = context;
        fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
    }

    public DefinitionsFragment addDefinitionsFragment(String word, String selectedLanguage) {
        Log.d("FragmentHelper", "getting called");
        if (!fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT)) {
            removeSettingsFragment();

            DefinitionsFragment definitionsFragment = DefinitionsFragment.newInstance(word, selectedLanguage);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .add(R.id.define_container, definitionsFragment, TAG_DEFINITIONS_FRAGMENT)
                    .commit();
            ((Activity)context).findViewById(R.id.define_container).setClickable(true);

            return definitionsFragment;
        }

        return (DefinitionsFragment)fragmentManager.findFragmentByTag(TAG_DEFINITIONS_FRAGMENT);
    }

    public void removeDefinitionsFragment() {
        if (fragmentIsPresent(TAG_DEFINITIONS_FRAGMENT)) {
            DefinitionsFragment definitionsFragment = (DefinitionsFragment)fragmentManager.findFragmentByTag(TAG_DEFINITIONS_FRAGMENT);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .remove(definitionsFragment)
                    .commit();
            ((Activity) context).findViewById(R.id.define_container).setClickable(false);
        }
    }


    public SettingsFragment addSettingsFragment(String selectedLanguage) {
        if (!fragmentIsPresent(TAG_SETTINGS_FRAGMENT)) {
            removeDefinitionsFragment();
            SettingsFragment settingsFragment = SettingsFragment.newInstance(selectedLanguage);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_alpha_in, R.anim.fade_alpha_out)
                    .add(R.id.settings_container, settingsFragment, TAG_SETTINGS_FRAGMENT)
                    .commit();

            View wrapper = ((Activity)context).findViewById(R.id.wrapper);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(FADE_IN_OUT_TIME);
            wrapper.startAnimation(alphaAnimation);
            ((Activity)context).findViewById(R.id.settings_container).setClickable(true);
            return settingsFragment;
        }

        return (SettingsFragment)fragmentManager.findFragmentByTag(TAG_SETTINGS_FRAGMENT);
    }

    public void removeSettingsFragment() {
        if (fragmentIsPresent(TAG_SETTINGS_FRAGMENT)) {
            SettingsFragment settingsFragment = (SettingsFragment) fragmentManager.findFragmentByTag(TAG_SETTINGS_FRAGMENT);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_alpha_in, R.anim.fade_alpha_out)
                    .remove(settingsFragment)
                    .commit();

            View wrapper = ((Activity)context).findViewById(R.id.wrapper);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(FADE_IN_OUT_TIME);
            wrapper.startAnimation(alphaAnimation);
            ((Activity)context).findViewById(R.id.settings_container).setClickable(false);
        }
    }

    public boolean fragmentIsPresent(String tag) {
        return fragmentManager.findFragmentByTag(tag) != null;
    }
}
