package com.coryswainston.smart.dictionary.schema.inflection;

import java.util.List;

public class HeadwordLemmatron {
    private String id;
    private String language;
    private List<LemmatronLexicalEntry> lexicalEntries;
    private String type;
    private String word;

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

    public List<LemmatronLexicalEntry> getLexicalEntries() {
        return lexicalEntries;
    }

    public void setLexicalEntries(List<LemmatronLexicalEntry> lexicalEntries) {
        this.lexicalEntries = lexicalEntries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeadwordLemmatron that = (HeadwordLemmatron) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null)
            return false;
        if (lexicalEntries != null ? !lexicalEntries.equals(that.lexicalEntries) : that.lexicalEntries != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return word != null ? word.equals(that.word) : that.word == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (lexicalEntries != null ? lexicalEntries.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (word != null ? word.hashCode() : 0);
        return result;
    }
}
