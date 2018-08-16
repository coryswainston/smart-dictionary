package com.coryswainston.smart.dictionary;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.DEFINITIONS;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.ENTRIES;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.LEXICAL_CATEGORY;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.LEXICAL_ENTRIES;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.RESULTS;
import static com.coryswainston.smart.dictionary.DictionaryResponseSchema.SENSES;

/**
 * Library for parsing JSON responses
 */

public class ParsingHelper {
    public static SpannableStringBuilder parseDefinitionsFromJson(String s) throws IOException,
            NullPointerException,
            IndexOutOfBoundsException,
            ClassCastException {

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

        ParsableJson<Object> result = new ParsableJson<>(s)
                .getList(RESULTS)
                .getObject(0);

        String word = result.getAsMap(String.class, String.class).get("word");
        stringBuilder.append(word);
        stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append("\n\n");

        ParsableJson<List<Object>> lexicalEntries = result.getList(LEXICAL_ENTRIES);

        for (ParsableJson<Object> lexicalEntry : lexicalEntries) {
            Map<String, String> lexicalEntryMap = lexicalEntry.getAsMap(String.class, String.class);
            String lexicalCategory = lexicalEntryMap.get(LEXICAL_CATEGORY).toLowerCase();
            stringBuilder.append(lexicalCategory);
            stringBuilder.setSpan(new StyleSpan(Typeface.ITALIC), stringBuilder.length() - lexicalCategory.length(),
                    stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append("\n");

            ParsableJson<List<Object>> senses = lexicalEntry.getList(ENTRIES)
                    .getObject(0)
                    .getList(SENSES);

            for (ParsableJson<Object> sense : senses) {
                List<String> definitions = sense.getList(DEFINITIONS).getAsList(String.class);
                String definition = definitions.get(0);

                int definitionNumber = senses.get().indexOf(sense.get()) + 1;
                stringBuilder.append(String.format("%s. %s%n", definitionNumber, definition));
            }
            stringBuilder.append('\n');
        }

        return stringBuilder;
    }
}
