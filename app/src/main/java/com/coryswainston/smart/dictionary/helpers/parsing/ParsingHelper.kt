package com.coryswainston.smart.dictionary.helpers.parsing

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.coryswainston.smart.dictionary.schema.Inflection
import com.coryswainston.smart.dictionary.schema.Lemmatron
import com.coryswainston.smart.dictionary.schema.RetrieveEntry
import com.coryswainston.smart.dictionary.services.DictionaryLookupService
import com.google.gson.Gson
import java.util.*

/**
 * Library for parsing JSON responses
 */
object ParsingHelper {
    /**
     * Parses the JSON dictionary response
     *
     * @param s the JSON response
     * @return a SpannableStringBuilder with the full formatted list of definitions
     * @throws ParsingException if anything goes wrong we will treat it the same for now
     */
    @JvmStatic
    @Throws(ParsingException::class)
    fun parseDefinitionsFromJson(s: String): SpannableStringBuilder? {
        if (s.startsWith(DictionaryLookupService.NO_INTERNET_ERROR)) {
            return null
        }
        return try {
            val stringBuilder = SpannableStringBuilder()
            var first = true
            val retrieveEntry = Gson().fromJson(s, RetrieveEntry::class.java)
            val results = retrieveEntry.results
            for (result in results) {
                val lexicalEntries = result.lexicalEntries
                for (lexicalEntry in lexicalEntries) {
                    val lexicalCategory = lexicalEntry.lexicalCategory.text
                    if (first) {
                        first = false
                    } else {
                        stringBuilder.append('\n')
                    }
                    stringBuilder.append(lexicalCategory.toLowerCase(Locale.getDefault()))
                    stringBuilder.setSpan(StyleSpan(Typeface.BOLD),
                            stringBuilder.length - lexicalCategory.length,
                            stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    stringBuilder.append("\n")
                    for (entry in lexicalEntry.entries) {
                        val senses = entry.senses
                        for (sense in senses) {
                            val definitions = sense.definitions
                            if (definitions.isNotEmpty()) {
                                val definitionNumber = senses.indexOf(sense) + 1
                                val definition = definitions[0]
                                stringBuilder.append(String.format("%s. %s%n", definitionNumber, definition))
                            }
                        }
                    }
                }
            }
            stringBuilder
        } catch (e: Exception) {
            e.printStackTrace()
            throw ParsingException(e)
        }
    }

    @JvmStatic
    @Throws(ParsingException::class)
    fun parseInflectionsFromResponse(s: String): List<Inflection>? {
        return if (s.startsWith(DictionaryLookupService.NO_INTERNET_ERROR)) {
            null
        } else try {
            val inflections: MutableList<Inflection> = ArrayList()
            val response = Gson().fromJson(s, Lemmatron::class.java)
            val results = response.results
            for (result in results) {
                val lexicalEntries = result.lexicalEntries
                for (lexicalEntry in lexicalEntries) {
                    inflections.addAll(lexicalEntry.inflectionOf)
                }
            }
            inflections
        } catch (e: Exception) {
            throw ParsingException(e)
        }
    }
}