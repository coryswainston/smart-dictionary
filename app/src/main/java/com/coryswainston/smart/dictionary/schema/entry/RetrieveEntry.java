package com.coryswainston.smart.dictionary.schema.entry;

import java.util.List;

/**
 * Model for the Oxford Dictionary API response for the entries endpoint.
 */

public class RetrieveEntry {
    private Object metadata;
    private List<HeadwordEntry> results;

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public List<HeadwordEntry> getResults() {
        return results;
    }

    public void setResults(List<HeadwordEntry> results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetrieveEntry that = (RetrieveEntry) o;

        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null)
            return false;
        return results != null ? results.equals(that.results) : that.results == null;
    }

    @Override
    public int hashCode() {
        int result = metadata != null ? metadata.hashCode() : 0;
        result = 31 * result + (results != null ? results.hashCode() : 0);
        return result;
    }
}
