package com.coryswainston.smart.dictionary.schema.inflection;

import com.coryswainston.smart.dictionary.schema.GrammaticalFeature;

import java.util.List;

public class LemmatronLexicalEntry {
    private List<GrammaticalFeature> grammaticalFeatures;
    private List<Inflection> inflectionOf;
    private String language;
    private String lexicalCategory;
    private String text;

    public List<GrammaticalFeature> getGrammaticalFeatures() {
        return grammaticalFeatures;
    }

    public void setGrammaticalFeatures(List<GrammaticalFeature> grammaticalFeatures) {
        this.grammaticalFeatures = grammaticalFeatures;
    }

    public List<Inflection> getInflectionOf() {
        return inflectionOf;
    }

    public void setInflectionOf(List<Inflection> inflectionOf) {
        this.inflectionOf = inflectionOf;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLexicalCategory() {
        return lexicalCategory;
    }

    public void setLexicalCategory(String lexicalCategory) {
        this.lexicalCategory = lexicalCategory;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LemmatronLexicalEntry that = (LemmatronLexicalEntry) o;

        if (grammaticalFeatures != null ? !grammaticalFeatures.equals(that.grammaticalFeatures) : that.grammaticalFeatures != null)
            return false;
        if (inflectionOf != null ? !inflectionOf.equals(that.inflectionOf) : that.inflectionOf != null)
            return false;
        if (language != null ? !language.equals(that.language) : that.language != null)
            return false;
        if (lexicalCategory != null ? !lexicalCategory.equals(that.lexicalCategory) : that.lexicalCategory != null)
            return false;
        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        int result = grammaticalFeatures != null ? grammaticalFeatures.hashCode() : 0;
        result = 31 * result + (inflectionOf != null ? inflectionOf.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (lexicalCategory != null ? lexicalCategory.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
