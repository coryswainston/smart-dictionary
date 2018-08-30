package com.coryswainston.smart.dictionary.schema.inflection;

import java.util.List;

/**
 * Model for the response given by Oxford Dictionary API inflections endpoint
 */

public class Lemmatron {
    private Object metadata;
    private List<HeadwordLemmatron> results;

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public List<HeadwordLemmatron> getResults() {
        return results;
    }

    public void setResults(List<HeadwordLemmatron> results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lemmatron lemmatron = (Lemmatron) o;

        if (metadata != null ? !metadata.equals(lemmatron.metadata) : lemmatron.metadata != null)
            return false;
        return results != null ? results.equals(lemmatron.results) : lemmatron.results == null;
    }

    @Override
    public int hashCode() {
        int result = metadata != null ? metadata.hashCode() : 0;
        result = 31 * result + (results != null ? results.hashCode() : 0);
        return result;
    }
}
