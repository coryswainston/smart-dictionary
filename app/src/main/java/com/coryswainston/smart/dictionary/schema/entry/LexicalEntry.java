package com.coryswainston.smart.dictionary.schema.entry;

import com.coryswainston.smart.dictionary.schema.GrammaticalFeature;
import com.coryswainston.smart.dictionary.schema.LexicalCategory;

import java.util.List;

public class LexicalEntry {
    private List<RelatedEntry> derivativeOf;
    private List<RelatedEntry> derivatives;
    private List<Entry> entries;
    private List<GrammaticalFeature> grammaticalFeatures;
    private String language;
    private LexicalCategory lexicalCategory;
    private List<CategorizedText> notes;
    private List<Pronunciation> pronunciations;
    private String text;
    private List<VariantForm> variantForms;

    public List<RelatedEntry> getDerivativeOf() {
        return derivativeOf;
    }

    public void setDerivativeOf(List<RelatedEntry> derivativeOf) {
        this.derivativeOf = derivativeOf;
    }

    public List<RelatedEntry> getDerivatives() {
        return derivatives;
    }

    public void setDerivatives(List<RelatedEntry> derivatives) {
        this.derivatives = derivatives;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<GrammaticalFeature> getGrammaticalFeatures() {
        return grammaticalFeatures;
    }

    public void setGrammaticalFeatures(List<GrammaticalFeature> grammaticalFeatures) {
        this.grammaticalFeatures = grammaticalFeatures;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LexicalCategory getLexicalCategory() {
        return lexicalCategory;
    }

    public void setLexicalCategory(LexicalCategory lexicalCategory) {
        this.lexicalCategory = lexicalCategory;
    }

    public List<CategorizedText> getNotes() {
        return notes;
    }

    public void setNotes(List<CategorizedText> notes) {
        this.notes = notes;
    }

    public List<Pronunciation> getPronunciations() {
        return pronunciations;
    }

    public void setPronunciations(List<Pronunciation> pronunciations) {
        this.pronunciations = pronunciations;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<VariantForm> getVariantForms() {
        return variantForms;
    }

    public void setVariantForms(List<VariantForm> variantForms) {
        this.variantForms = variantForms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LexicalEntry that = (LexicalEntry) o;

        if (derivativeOf != null ? !derivativeOf.equals(that.derivativeOf) : that.derivativeOf != null)
            return false;
        if (derivatives != null ? !derivatives.equals(that.derivatives) : that.derivatives != null)
            return false;
        if (entries != null ? !entries.equals(that.entries) : that.entries != null) return false;
        if (grammaticalFeatures != null ? !grammaticalFeatures.equals(that.grammaticalFeatures) : that.grammaticalFeatures != null)
            return false;
        if (language != null ? !language.equals(that.language) : that.language != null)
            return false;
        if (lexicalCategory != null ? !lexicalCategory.equals(that.lexicalCategory) : that.lexicalCategory != null)
            return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (pronunciations != null ? !pronunciations.equals(that.pronunciations) : that.pronunciations != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        return variantForms != null ? variantForms.equals(that.variantForms) : that.variantForms == null;
    }

    @Override
    public int hashCode() {
        int result = derivativeOf != null ? derivativeOf.hashCode() : 0;
        result = 31 * result + (derivatives != null ? derivatives.hashCode() : 0);
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        result = 31 * result + (grammaticalFeatures != null ? grammaticalFeatures.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (lexicalCategory != null ? lexicalCategory.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (pronunciations != null ? pronunciations.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (variantForms != null ? variantForms.hashCode() : 0);
        return result;
    }
}
