package com.coryswainston.smart.dictionary.config;

import java.util.List;

/**
 * Data for a word inflection
 */

public class Inflection {
    private String id;
    private List<String> grammaticalFeatures;
    private List<String> inflectionOf;
    private String lexicalCategory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getGrammaticalFeatures() {
        return grammaticalFeatures;
    }

    public void setGrammaticalFeatures(List<String> grammaticalFeatures) {
        this.grammaticalFeatures = grammaticalFeatures;
    }

    public List<String> getInflectionOf() {
        return inflectionOf;
    }

    public void setInflectionOf(List<String> inflectionOf) {
        this.inflectionOf = inflectionOf;
    }

    public String getLexicalCategory() {
        return lexicalCategory;
    }

    public void setLexicalCategory(String lexicalCategory) {
        this.lexicalCategory = lexicalCategory;
    }
}
