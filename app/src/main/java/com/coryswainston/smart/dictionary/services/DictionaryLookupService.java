package com.coryswainston.smart.dictionary.services;

import android.os.AsyncTask;

import com.coryswainston.smart.dictionary.util.DictionaryNetworkUtils;
import com.coryswainston.smart.dictionary.schema.inflection.Inflection;
import com.coryswainston.smart.dictionary.util.Key;
import com.coryswainston.smart.dictionary.helpers.parsing.ParsingHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

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

    private static final String BASE_URL = "https://od-api.oxforddictionaries.com:443/api/v1";
    private static final String ENTRIES = "entries";
    private static final String INFLECTIONS = "inflections";
    private String language;
    private String wordToLookup;

    private DictionaryNetworkUtils utils;

    private Callback callback;

    public DictionaryLookupService() {
        utils = new DictionaryNetworkUtils();
    }

    /**
     * Constructor for testing
     */
    public DictionaryLookupService(DictionaryNetworkUtils utils) {
        this.utils = utils;
    }

    @Override
    protected String doInBackground(String... params) {
        if (language == null) {
            language = LANGUAGE_EN;
        }

        wordToLookup = params[0];

        try {
            HttpsURLConnection urlConnection = connectToApi(INFLECTIONS, wordToLookup);
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String response = getJsonFromStream(reader);
            urlConnection.getInputStream().close();

            if (urlConnection.getResponseCode() == 200) {
                String wordToDefine = null;
                List<Inflection> inflections = ParsingHelper.parseInflectionsFromResponse(response);
                for (Inflection inflection : inflections) {
                    if (inflection.getId().equals(wordToLookup)) {
                        wordToDefine = wordToLookup;
                        break;
                    }
                }
                if (wordToDefine == null) {
                    wordToDefine = inflections.get(0).getId();
                }

                HttpsURLConnection definitionConnection = connectToApi(ENTRIES, wordToDefine);
                BufferedReader definitionReader = new BufferedReader(new InputStreamReader(definitionConnection.getInputStream()));
                String entriesResponse = getJsonFromStream(definitionReader);
                urlConnection.getInputStream().close();

                return entriesResponse;
            }

            throw new Exception("Word not found");
        } catch (UnknownHostException e) {
            return NO_INTERNET_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private HttpsURLConnection connectToApi(String operation, String word) throws Exception {
        HttpsURLConnection urlConnection = utils.connectToUrl(String.format("%s/%s/%s/%s", BASE_URL, operation, language, word));
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("app_id", Key.APP_ID);
        urlConnection.setRequestProperty("app_key", Key.APP_KEY);
        return urlConnection;
    }

    private String getJsonFromStream(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
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

