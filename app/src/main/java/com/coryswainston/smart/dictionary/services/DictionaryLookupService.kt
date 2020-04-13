package com.coryswainston.smart.dictionary.services

import android.os.AsyncTask
import com.coryswainston.smart.dictionary.helpers.parsing.ParsingHelper.parseInflectionsFromResponse
import com.coryswainston.smart.dictionary.util.DictionaryNetworkUtils
import com.coryswainston.smart.dictionary.util.Key
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

/**
 * For looking up dictionary definitions using the Oxford API.
 * Much of this is currently copied from their documentation.
 */
class DictionaryLookupService : AsyncTask<String?, Int?, String> {
    private var language: String? = null
    private var wordToLookup: String? = null
    private var utils: DictionaryNetworkUtils
    private var callback: Callback? = null

    constructor() {
        utils = DictionaryNetworkUtils()
    }

    /**
     * Constructor for testing
     */
    constructor(utils: DictionaryNetworkUtils) {
        this.utils = utils
    }

    override fun doInBackground(vararg params: String?): String {
        if (language == null) {
            language = LANGUAGE_EN
        }
        wordToLookup = params[0]
        return try {
            val urlConnection = connectToApi(INFLECTIONS, wordToLookup)
            val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val response = getJsonFromStream(reader)
            urlConnection.inputStream.close()
            if (urlConnection.responseCode == 200) {
                var wordToDefine: String? = null
                val inflections = parseInflectionsFromResponse(response)
                for ((id) in inflections!!) {
                    if (id == wordToLookup) {
                        wordToDefine = wordToLookup
                        break
                    }
                }
                if (wordToDefine == null) {
                    wordToDefine = inflections[0].id
                }
                val definitionConnection = connectToApi(ENTRIES, wordToDefine)
                val definitionReader = BufferedReader(InputStreamReader(definitionConnection.inputStream))
                val entriesResponse = getJsonFromStream(definitionReader)
                urlConnection.inputStream.close()
                return entriesResponse
            }
            throw Exception("Word not found")
        } catch (e: UnknownHostException) {
            NO_INTERNET_ERROR
        } catch (e: Exception) {
            e.printStackTrace()
            e.toString()
        }
    }

    @Throws(Exception::class)
    private fun connectToApi(operation: String, word: String?): HttpsURLConnection {
        val urlConnection = utils.connectToUrl(String.format("%s/%s/%s/%s", BASE_URL, operation, language, word))
        urlConnection.setRequestProperty("Accept", "application/json")
        urlConnection.setRequestProperty("app_id", Key.APP_ID)
        urlConnection.setRequestProperty("app_key", Key.APP_KEY)
        return urlConnection
    }

    @Throws(IOException::class)
    private fun getJsonFromStream(reader: BufferedReader): String {
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
            stringBuilder.append('\n')
        }
        return stringBuilder.toString()
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        callback!!.callback(wordToLookup, result)
    }

    fun withCallback(callback: Callback?): DictionaryLookupService {
        this.callback = callback
        return this
    }

    fun withLanguage(language: String?): DictionaryLookupService {
        this.language = language
        return this
    }

    interface Callback {
        fun callback(word: String?, definitions: String?)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "DictionaryLookupService"
        const val LANGUAGE_EN = "en"
        const val LANGUAGE_ES = "es"
        const val NO_INTERNET_ERROR = "no_internet"
        private const val BASE_URL = "https://od-api.oxforddictionaries.com/api/v2"
        private const val ENTRIES = "entries"
        private const val INFLECTIONS = "lemmas"
    }
}