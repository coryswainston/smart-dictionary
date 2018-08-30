package com.coryswainston.smart.dictionary.schema.entry;

import com.coryswainston.smart.dictionary.schema.GrammaticalFeature;

import java.util.List;

public class Entry {
    private List<String> etymologies;
    private List<GrammaticalFeature> grammaticalFeatures;
    private String homographNumber;
    private List<CategorizedText> notes;
    private List<Pronunciation> pronunciations;
    private List<Sense> senses;
    private List<VariantForm> variantForms;

    public List<String> getEtymologies() {
        return etymologies;
    }

    public void setEtymologies(List<String> etymologies) {
        this.etymologies = etymologies;
    }

    public List<GrammaticalFeature> getGrammaticalFeatures() {
        return grammaticalFeatures;
    }

    public void setGrammaticalFeatures(List<GrammaticalFeature> grammaticalFeatures) {
        this.grammaticalFeatures = grammaticalFeatures;
    }

    public String getHomographNumber() {
        return homographNumber;
    }

    public void setHomographNumber(String homographNumber) {
        this.homographNumber = homographNumber;
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

    public List<Sense> getSenses() {
        return senses;
    }

    public void setSenses(List<Sense> senses) {
        this.senses = senses;
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

        Entry entry = (Entry) o;

        if (etymologies != null ? !etymologies.equals(entry.etymologies) : entry.etymologies != null)
            return false;
        if (grammaticalFeatures != null ? !grammaticalFeatures.equals(entry.grammaticalFeatures) : entry.grammaticalFeatures != null)
            return false;
        if (homographNumber != null ? !homographNumber.equals(entry.homographNumber) : entry.homographNumber != null)
            return false;
        if (notes != null ? !notes.equals(entry.notes) : entry.notes != null) return false;
        if (pronunciations != null ? !pronunciations.equals(entry.pronunciations) : entry.pronunciations != null)
            return false;
        if (senses != null ? !senses.equals(entry.senses) : entry.senses != null) return false;
        return variantForms != null ? variantForms.equals(entry.variantForms) : entry.variantForms == null;
    }

    @Override
    public int hashCode() {
        int result = etymologies != null ? etymologies.hashCode() : 0;
        result = 31 * result + (grammaticalFeatures != null ? grammaticalFeatures.hashCode() : 0);
        result = 31 * result + (homographNumber != null ? homographNumber.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (pronunciations != null ? pronunciations.hashCode() : 0);
        result = 31 * result + (senses != null ? senses.hashCode() : 0);
        result = 31 * result + (variantForms != null ? variantForms.hashCode() : 0);
        return result;
    }
}
