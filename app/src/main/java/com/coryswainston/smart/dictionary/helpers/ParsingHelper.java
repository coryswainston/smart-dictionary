package com.coryswainston.smart.dictionary.helpers;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import java.util.List;
import java.util.Map;

import static com.coryswainston.smart.dictionary.config.DictionaryResponseSchema.*;

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

        try {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

            ParsableJson<Object> result = new ParsableJson<>(s)
                    .getList(RESULTS)
                    .getObject(0);


            ParsableJson<List<Object>> lexicalEntries = result.getList(LEXICAL_ENTRIES);

            for (ParsableJson<Object> lexicalEntry : lexicalEntries) {
                Map<String, String> lexicalEntryMap = lexicalEntry.getAsMap(String.class, String.class);
                String lexicalCategory = lexicalEntryMap.get(LEXICAL_CATEGORY).toLowerCase();
                stringBuilder.append(lexicalCategory);
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), stringBuilder.length() - lexicalCategory.length(),
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
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
