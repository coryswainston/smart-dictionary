package com.coryswainston.smart.dictionary.schema.entry;

import java.util.List;

public class Pronunciation {
    private String audioFile;
    private List<String> dialects;
    private String phoneticNotation;
    private String phoneticSpelling;
    private List<String> regions;

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public List<String> getDialects() {
        return dialects;
    }

    public void setDialects(List<String> dialects) {
        this.dialects = dialects;
    }

    public String getPhoneticNotation() {
        return phoneticNotation;
    }

    public void setPhoneticNotation(String phoneticNotation) {
        this.phoneticNotation = phoneticNotation;
    }

    public String getPhoneticSpelling() {
        return phoneticSpelling;
    }

    public void setPhoneticSpelling(String phoneticSpelling) {
        this.phoneticSpelling = phoneticSpelling;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pronunciation that = (Pronunciation) o;

        if (audioFile != null ? !audioFile.equals(that.audioFile) : that.audioFile != null)
            return false;
        if (dialects != null ? !dialects.equals(that.dialects) : that.dialects != null)
            return false;
        if (phoneticNotation != null ? !phoneticNotation.equals(that.phoneticNotation) : that.phoneticNotation != null)
            return false;
        if (phoneticSpelling != null ? !phoneticSpelling.equals(that.phoneticSpelling) : that.phoneticSpelling != null)
            return false;
        return regions != null ? regions.equals(that.regions) : that.regions == null;
    }

    @Override
    public int hashCode() {
        int result = audioFile != null ? audioFile.hashCode() : 0;
        result = 31 * result + (dialects != null ? dialects.hashCode() : 0);
        result = 31 * result + (phoneticNotation != null ? phoneticNotation.hashCode() : 0);
        result = 31 * result + (phoneticSpelling != null ? phoneticSpelling.hashCode() : 0);
        result = 31 * result + (regions != null ? regions.hashCode() : 0);
        return result;
    }
}
