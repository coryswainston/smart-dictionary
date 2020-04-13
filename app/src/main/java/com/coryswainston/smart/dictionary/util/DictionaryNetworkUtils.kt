package com.coryswainston.smart.dictionary.util

import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * A wrapper for testing
 */
class DictionaryNetworkUtils {
    @Throws(IOException::class)
    fun connectToUrl(path: String?): HttpsURLConnection {
        return URL(path).openConnection() as HttpsURLConnection
    }
}