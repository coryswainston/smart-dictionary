package com.coryswainston.smart.dictionary.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A wrapper for testing
 */

public class DictionaryNetworkUtils {
    public HttpsURLConnection connectToUrl(String path) throws IOException {
        return (HttpsURLConnection) (new URL(path).openConnection());
    }
}
