package com.coryswainston.smart.dictionary.schema.entry;

import java.util.List;

public class Sense {
    private List<String> crossReferenceMarkers;
    private List<CrossReference> crossReferences;
    private List<String> definitions;
    private List<String> domains;
    private List<Example> examples;
    private String id;
    private List<CategorizedText> notes;
    private List<Pronunciation> pronunciations;
    private List<String> regions;
    private List<String> registers;
    private List<String> shortDefinitions;
    private List<Sense> subsenses;
    private List<ThesaurusLink> thesaurusLinks;
    private List<Translation> translations;
    private List<VariantForm> variantForms;

    public List<String> getCrossReferenceMarkers() {
        return crossReferenceMarkers;
    }

    public void setCrossReferenceMarkers(List<String> crossReferenceMarkers) {
        this.crossReferenceMarkers = crossReferenceMarkers;
    }

    public List<CrossReference> getCrossReferences() {
        return crossReferences;
    }

    public void setCrossReferences(List<CrossReference> crossReferences) {
        this.crossReferences = crossReferences;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<String> getRegisters() {
        return registers;
    }

    public void setRegisters(List<String> registers) {
        this.registers = registers;
    }

    public List<String> getShortDefinitions() {
        return shortDefinitions;
    }

    public void setShortDefinitions(List<String> shortDefinitions) {
        this.shortDefinitions = shortDefinitions;
    }

    public List<Sense> getSubsenses() {
        return subsenses;
    }

    public void setSubsenses(List<Sense> subsenses) {
        this.subsenses = subsenses;
    }

    public List<ThesaurusLink> getThesaurusLinks() {
        return thesaurusLinks;
    }

    public void setThesaurusLinks(List<ThesaurusLink> thesaurusLinks) {
        this.thesaurusLinks = thesaurusLinks;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
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

        Sense sense = (Sense) o;

        if (crossReferenceMarkers != null ? !crossReferenceMarkers.equals(sense.crossReferenceMarkers) : sense.crossReferenceMarkers != null)
            return false;
        if (crossReferences != null ? !crossReferences.equals(sense.crossReferences) : sense.crossReferences != null)
            return false;
        if (definitions != null ? !definitions.equals(sense.definitions) : sense.definitions != null)
            return false;
        if (domains != null ? !domains.equals(sense.domains) : sense.domains != null) return false;
        if (examples != null ? !examples.equals(sense.examples) : sense.examples != null)
            return false;
        if (id != null ? !id.equals(sense.id) : sense.id != null) return false;
        if (notes != null ? !notes.equals(sense.notes) : sense.notes != null) return false;
        if (pronunciations != null ? !pronunciations.equals(sense.pronunciations) : sense.pronunciations != null)
            return false;
        if (regions != null ? !regions.equals(sense.regions) : sense.regions != null) return false;
        if (registers != null ? !registers.equals(sense.registers) : sense.registers != null)
            return false;
        if (shortDefinitions != null ? !shortDefinitions.equals(sense.shortDefinitions) : sense.shortDefinitions != null)
            return false;
        if (subsenses != null ? !subsenses.equals(sense.subsenses) : sense.subsenses != null)
            return false;
        if (thesaurusLinks != null ? !thesaurusLinks.equals(sense.thesaurusLinks) : sense.thesaurusLinks != null)
            return false;
        if (translations != null ? !translations.equals(sense.translations) : sense.translations != null)
            return false;
        return variantForms != null ? variantForms.equals(sense.variantForms) : sense.variantForms == null;
    }

    @Override
    public int hashCode() {
        int result = crossReferenceMarkers != null ? crossReferenceMarkers.hashCode() : 0;
        result = 31 * result + (crossReferences != null ? crossReferences.hashCode() : 0);
        result = 31 * result + (definitions != null ? definitions.hashCode() : 0);
        result = 31 * result + (domains != null ? domains.hashCode() : 0);
        result = 31 * result + (examples != null ? examples.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (pronunciations != null ? pronunciations.hashCode() : 0);
        result = 31 * result + (regions != null ? regions.hashCode() : 0);
        result = 31 * result + (registers != null ? registers.hashCode() : 0);
        result = 31 * result + (shortDefinitions != null ? shortDefinitions.hashCode() : 0);
        result = 31 * result + (subsenses != null ? subsenses.hashCode() : 0);
        result = 31 * result + (thesaurusLinks != null ? thesaurusLinks.hashCode() : 0);
        result = 31 * result + (translations != null ? translations.hashCode() : 0);
        result = 31 * result + (variantForms != null ? variantForms.hashCode() : 0);
        return result;
    }
}
