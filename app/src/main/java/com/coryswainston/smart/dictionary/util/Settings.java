package com.coryswainston.smart.dictionary.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioGroup;

import com.coryswainston.smart.dictionary.R;

import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_EN;
import static com.coryswainston.smart.dictionary.services.DictionaryLookupService.LANGUAGE_ES;

/**
 * A class to help with settings
 */

public class Settings {

    private static final String OK = "OK";
    private static final String CANCEL = "CANCEL";

    private static final String KEY_LEXIGLASS = "lexiglass";
    private static final String KEY_LANGUAGE = "language";

    public static void showDialog(Context context) {
        Resources res = context.getResources();
        final String selectLanguage = res.getString(R.string.language_instructions);

        final Activity activity = (Activity) context;

        ConstraintLayout cl = (ConstraintLayout)View.inflate(context, R.layout.language_radio_group, null);
        final RadioGroup languages = cl.findViewById(R.id.radioGroup);
        String selectedLanguage = loadLanguagePreference(context);
        if (selectedLanguage.equals(LANGUAGE_EN)) {
            languages.check(R.id.english_radio);
        } else {
            languages.check(R.id.spanish_radio);
        }

        final AlertDialog settingsDialog = new AlertDialog.Builder(activity)
                .setTitle(selectLanguage)
                .setView(cl)
                .setNegativeButton(CANCEL, null)
                .create();

        settingsDialog.setButton(DialogInterface.BUTTON_POSITIVE, OK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String language;
                        if (languages.getCheckedRadioButtonId() == R.id.english_radio) {
                            language = LANGUAGE_EN;
                        } else {
                            language = LANGUAGE_ES;
                        }
                        saveLanguagePreference(activity, language);
                    }
                });
        settingsDialog.show();
    }

    private static void saveLanguagePreference(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_LEXIGLASS, 0);
        sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply();
    }

    public static String loadLanguagePreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_LEXIGLASS, 0);
        return sharedPreferences.getString(KEY_LANGUAGE, LANGUAGE_EN);
    }
}
