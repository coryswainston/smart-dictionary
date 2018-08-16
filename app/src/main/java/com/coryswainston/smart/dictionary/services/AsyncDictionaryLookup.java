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

public class AsyncDictionaryLookup extends AsyncTask<String, Integer, String> {

    private OnCompleteListener listener;
    private String lang;

    @Override
    protected String doInBackground(String... params) {
        if (lang == null) {
            lang = "en";
        }

        try {
            URL url = new URL("https://od-api.oxforddictionaries.com:443/api/v1/entries/" + lang + "/" + params[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", Key.APP_ID);
            urlConnection.setRequestProperty("app_key", Key.APP_KEY);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
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

    public AsyncDictionaryLookup withListener(OnCompleteListener listener) {
        this.listener = listener;
        return this;
    }

    public AsyncDictionaryLookup withLang(String lang) {
        this.lang = lang;
        return this;
    }
}

