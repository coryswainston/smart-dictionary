package com.coryswainston.smart.dictionary.helpers.parsing;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.coryswainston.smart.dictionary.util.Inflection;
import com.coryswainston.smart.dictionary.services.DictionaryLookupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.coryswainston.smart.dictionary.util.DictionaryResponseSchema.*;

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

                ParsableJson<List<Object>> entries = lexicalEntry.getList(ENTRIES);

                if (entries != null && !entries.get().isEmpty()) {
                    ParsableJson<List<Object>> senses = entries.getObject(0)
                            .getList(SENSES);

                    for (ParsableJson<Object> sense : senses) {
                        List<String> definitions = sense.getList(DEFINITIONS).getAsList(String.class);
                        if (definitions != null && !definitions.isEmpty()) {
                            String definition = definitions.get(0);

                            int definitionNumber = senses.get().indexOf(sense.get()) + 1;
                            stringBuilder.append(String.format("%s. %s%n", definitionNumber, definition));
                        }
                    }
                    stringBuilder.append('\n');
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

            ParsableJson<Object> result = new ParsableJson<>(s)
                    .getList(RESULTS)
                    .getObject(0);

            ParsableJson<List<Object>> lexicalEntries = result.getList(LEXICAL_ENTRIES);

            for (ParsableJson<Object> lexicalEntry : lexicalEntries) {
                Inflection inflection = new Inflection();

                Map<String, String> lexicalEntryMap = lexicalEntry.getAsMap(String.class, String.class);
                inflection.setId(lexicalEntryMap.get(ID));

                inflection.setLexicalCategory(lexicalEntryMap.get(LEXICAL_CATEGORY).toLowerCase());

                ParsableJson<List<Object>> grammaticalFeatures = lexicalEntry.getList(GRAMMATICAL_FEATURES);
                inflection.setGrammaticalFeatures(new ArrayList<String>());

                for (ParsableJson<Object> grammaticalFeature : grammaticalFeatures) {
                    inflection.getGrammaticalFeatures().add(grammaticalFeature.getObject("text").getAsObject(String.class));
                }

                ParsableJson<List<Object>> inflectionOf = lexicalEntry.getList(INFLECTION_OF);
                inflection.setInflectionOf(new ArrayList<String>());
                for (ParsableJson<Object> word : inflectionOf) {
                    inflection.getInflectionOf().add(word.getObject(ID).getAsObject(String.class));
                }

                inflections.add(inflection);
            }

            return inflections;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
