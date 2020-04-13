package com.coryswainston.smart.dictionary.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.RadioGroup
import com.coryswainston.smart.dictionary.R
import com.coryswainston.smart.dictionary.services.DictionaryLookupService
import java.util.*

/**
 * A class to help with settings
 */
object Settings {
    private const val KEY_LEXIGLASS = "lexiglass"
    private const val KEY_LANGUAGE = "language"

    fun showDialog(context: Context) {
        val res = context.resources
        val selectLanguage = res.getString(R.string.language_instructions)

        val activity = context as Activity
        val cl = View.inflate(context, R.layout.language_radio_group, null) as ConstraintLayout
        val languages = cl.findViewById<RadioGroup>(R.id.radioGroup)

        val selectedLanguage = loadLanguagePreference(context)
        if (selectedLanguage == DictionaryLookupService.LANGUAGE_EN) {
            languages.check(R.id.english_radio)
        } else {
            languages.check(R.id.spanish_radio)
        }

        val settingsDialog = AlertDialog.Builder(activity)
                .setTitle(selectLanguage)
                .setView(cl)
                .setNegativeButton(context.getResources().getString(R.string.cancel), null)
                .create()
        settingsDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.ok)) { _, _ ->
            val language: String = if (languages.checkedRadioButtonId == R.id.english_radio) {
                DictionaryLookupService.LANGUAGE_EN
            } else {
                DictionaryLookupService.LANGUAGE_ES
            }
            saveLanguagePreference(activity, language)
        }
        settingsDialog.show()
    }

    private fun saveLanguagePreference(context: Context, value: String) {
        val sharedPreferences = context.getSharedPreferences(KEY_LEXIGLASS, 0)
        sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()
    }

    @JvmStatic
    fun loadLanguagePreference(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(KEY_LEXIGLASS, 0)
        return sharedPreferences.getString(KEY_LANGUAGE,
                if (Locale.getDefault().language == DictionaryLookupService.LANGUAGE_ES) {
                    DictionaryLookupService.LANGUAGE_ES
                } else {
                    DictionaryLookupService.LANGUAGE_EN
                })!!
    }
}