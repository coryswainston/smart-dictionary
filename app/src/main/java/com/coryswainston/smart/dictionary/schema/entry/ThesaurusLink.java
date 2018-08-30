package com.coryswainston.smart.dictionary.schema.entry;

public class ThesaurusLink {
    private String entryId;
    private String senseId;

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getSenseId() {
        return senseId;
    }

    public void setSenseId(String senseId) {
        this.senseId = senseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThesaurusLink that = (ThesaurusLink) o;

        if (entryId != null ? !entryId.equals(that.entryId) : that.entryId != null) return false;
        return senseId != null ? senseId.equals(that.senseId) : that.senseId == null;
    }

    @Override
    public int hashCode() {
        int result = entryId != null ? entryId.hashCode() : 0;
        result = 31 * result + (senseId != null ? senseId.hashCode() : 0);
        return result;
    }
}
