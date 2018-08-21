package com.coryswainston.smart.dictionary.services;

import android.os.AsyncTask;
import android.util.Log;

import com.coryswainston.smart.dictionary.config.Key;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

/**
 * For looking up dictionary definitions using the Oxford API.
 * Much of this is currently copied from their documentation.
 */

public class DictionaryLookupService extends AsyncTask<String, Integer, String> {
    private static final String TAG = "DictionaryLookupService";

    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_ES = "es";

    public static final String NO_INTERNET_ERROR = "no_internet";

    private static final String BASE_URL = "https://od-api.oxforddictionaries.com:443/api/v1/entries";
    private String language;
    private String wordToLookup;

    private Callback callback;

    @Override
    protected String doInBackground(String... params) {
        if (language == null) {
            language = LANGUAGE_EN;
        }

        wordToLookup = params[0];

        try {
            URL url = new URL( String.format("%s/%s/%s", BASE_URL, language, wordToLookup));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", Key.APP_ID);
            urlConnection.setRequestProperty("app_key", Key.APP_KEY);

            Log.d(TAG, "Response code is " + urlConnection.getResponseCode());

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            Log.d(TAG, "did it get to this point");
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }

            urlConnection.getInputStream().close();

            return stringBuilder.toString();
        } catch (UnknownHostException e) {
            return NO_INTERNET_ERROR;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return e.toString();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        callback.callback(wordToLookup, result);
    }

    public DictionaryLookupService withCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public DictionaryLookupService withLanguage(String language) {
        this.language = language;
        return this;
    }

    public interface Callback {
        void callback(String word, String definitions);
    }
}

