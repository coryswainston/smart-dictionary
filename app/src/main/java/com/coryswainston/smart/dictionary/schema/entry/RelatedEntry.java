package com.coryswainston.smart.dictionary.schema.entry;

import java.util.List;

public class RelatedEntry {
    private List<String> domains;
    private String id;
    private String language;
    private List<String> regions;
    private List<String> registers;
    private String text;

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

        RelatedEntry that = (RelatedEntry) o;

        if (domains != null ? !domains.equals(that.domains) : that.domains != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null)
            return false;
        if (regions != null ? !regions.equals(that.regions) : that.regions != null) return false;
        if (registers != null ? !registers.equals(that.registers) : that.registers != null)
            return false;
        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        int result = domains != null ? domains.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (regions != null ? regions.hashCode() : 0);
        result = 31 * result + (registers != null ? registers.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
