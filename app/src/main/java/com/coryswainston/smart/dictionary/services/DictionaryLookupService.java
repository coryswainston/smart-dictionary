package com.coryswainston.smart.dictionary.services;

import android.os.AsyncTask;

import com.coryswainston.smart.dictionary.listeners.OnCompleteListener;
import com.coryswainston.smart.dictionary.config.Key;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * For looking up dictionary definitions using the Oxford API.
 * Much of this is currently copied from their documentation.
 */

public class DictionaryLookupService extends AsyncTask<String, Integer, String> {

    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_ES = "es";

    private static final String BASE_URL = "https://od-api.oxforddictionaries.com:443/api/v1/entries";
    private String language;

    private OnCompleteListener listener;

    @Override
    protected String doInBackground(String... params) {
        if (language == null) {
            language = LANGUAGE_EN;
        }

        try {
            URL url = new URL( String.format("%s/%s/%s", BASE_URL, language, params[0]));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", Key.APP_ID);
            urlConnection.setRequestProperty("app_key", Key.APP_KEY);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }

            urlConnection.getInputStream().close();

            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        listener.onComplete(result);
    }

    public DictionaryLookupService withListener(OnCompleteListener listener) {
        this.listener = listener;
        return this;
    }

    public DictionaryLookupService withLanguage(String language) {
        this.language = language;
        return this;
    }
}

