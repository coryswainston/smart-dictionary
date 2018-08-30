package com.coryswainston.smart.dictionary.schema.entry;

import java.util.List;

public class Example {
    private List<String> definitions;
    private List<String> domains;
    private List<CategorizedText> notes;
    private List<String> regions;
    private List<String> registers;
    private List<String> senseIds;
    private String text;
    private List<Translation> translations;

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

    public List<CategorizedText> getNotes() {
        return notes;
    }

    public void setNotes(List<CategorizedText> notes) {
        this.notes = notes;
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

    public List<String> getSenseIds() {
        return senseIds;
    }

    public void setSenseIds(List<String> senseIds) {
        this.senseIds = senseIds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Example example = (Example) o;

        if (definitions != null ? !definitions.equals(example.definitions) : example.definitions != null)
            return false;
        if (domains != null ? !domains.equals(example.domains) : example.domains != null)
            return false;
        if (notes != null ? !notes.equals(example.notes) : example.notes != null) return false;
        if (regions != null ? !regions.equals(example.regions) : example.regions != null)
            return false;
        if (registers != null ? !registers.equals(example.registers) : example.registers != null)
            return false;
        if (senseIds != null ? !senseIds.equals(example.senseIds) : example.senseIds != null)
            return false;
        if (text != null ? !text.equals(example.text) : example.text != null) return false;
        return translations != null ? translations.equals(example.translations) : example.translations == null;
    }

    @Override
    public int hashCode() {
        int result = definitions != null ? definitions.hashCode() : 0;
        result = 31 * result + (domains != null ? domains.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (regions != null ? regions.hashCode() : 0);
        result = 31 * result + (registers != null ? registers.hashCode() : 0);
        result = 31 * result + (senseIds != null ? senseIds.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (translations != null ? translations.hashCode() : 0);
        return result;
    }
}
