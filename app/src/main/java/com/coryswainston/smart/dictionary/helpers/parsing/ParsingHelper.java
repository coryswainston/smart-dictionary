package com.coryswainston.smart.dictionary.helpers.parsing;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.coryswainston.smart.dictionary.schema.entry.Entry;
import com.coryswainston.smart.dictionary.schema.entry.HeadwordEntry;
import com.coryswainston.smart.dictionary.schema.entry.LexicalEntry;
import com.coryswainston.smart.dictionary.schema.entry.RetrieveEntry;
import com.coryswainston.smart.dictionary.schema.entry.Sense;
import com.coryswainston.smart.dictionary.schema.inflection.HeadwordLemmatron;
import com.coryswainston.smart.dictionary.schema.inflection.Inflection;
import com.coryswainston.smart.dictionary.schema.inflection.Lemmatron;
import com.coryswainston.smart.dictionary.schema.inflection.LemmatronLexicalEntry;
import com.coryswainston.smart.dictionary.services.DictionaryLookupService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Library for parsing JSON responses
 */

public class ParsingHelper {

    /**
     * Parses the JSON dictionary response
     *
     * @param s the JSON response
     * @return a SpannableStringBuilder with the full formatted list of definitions
     * @throws ParsingException if anything goes wrong we will treat it the same for now
     */
    public static SpannableStringBuilder parseDefinitionsFromJson(String s) throws ParsingException {

        if (s.startsWith(DictionaryLookupService.NO_INTERNET_ERROR))  {
            return null;
        }

        try {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

            boolean first = true;
            RetrieveEntry retrieveEntry = new Gson().fromJson(s, RetrieveEntry.class);
            List<HeadwordEntry> results  = retrieveEntry.getResults();
            if (results != null) {
                for (HeadwordEntry result : results) {
                    if (result != null) {
                        List<LexicalEntry> lexicalEntries = result.getLexicalEntries();
                        for (LexicalEntry lexicalEntry : lexicalEntries) {
                            if (lexicalEntry != null) {
                                String lexicalCategory = lexicalEntry.getLexicalCategory();
                                if (lexicalCategory != null) {
                                    if (first) {
                                        first = false;
                                    } else {
                                        stringBuilder.append('\n');
                                    }
                                    stringBuilder.append(lexicalCategory.toLowerCase());
                                    stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                                            stringBuilder.length() - lexicalCategory.length(),
                                            stringBuilder.length(),
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    stringBuilder.append("\n");
                                }

                                List<Entry> entries = lexicalEntry.getEntries();
                                if (entries != null) {
                                    for(Entry entry : lexicalEntry.getEntries()) {
                                        if (entry != null) {
                                            List<Sense> senses = entry.getSenses();
                                            if (senses != null) {
                                                for (Sense sense : senses) {
                                                    List<String> definitions = sense.getDefinitions();
                                                    if (definitions != null && !definitions.isEmpty()) {
                                                        int definitionNumber = senses.indexOf(sense) + 1;
                                                        String definition = definitions.get(0);
                                                        stringBuilder.append(String.format("%s. %s%n", definitionNumber, definition));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return stringBuilder;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParsingException(e);
        }
    }

    public static List<Inflection> parseInflectionsFromResponse(String s) throws ParsingException {
        if (s.startsWith(DictionaryLookupService.NO_INTERNET_ERROR))  {
            return null;
        }

        try {
            List<Inflection> inflections = new ArrayList<>();

            Lemmatron response = new Gson().fromJson(s, Lemmatron.class);
            List<HeadwordLemmatron> results = response.getResults();

            if (results != null) {
                for (HeadwordLemmatron result : results) {
                    if (result != null) {
                        List<LemmatronLexicalEntry> lexicalEntries = result.getLexicalEntries();
                        if (lexicalEntries != null) {
                            for (LemmatronLexicalEntry lexicalEntry : lexicalEntries) {
                                if (lexicalEntry != null) {
                                    inflections.addAll(lexicalEntry.getInflectionOf());
                                }
                            }
                        }
                    }
                }
            }

            return inflections;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
