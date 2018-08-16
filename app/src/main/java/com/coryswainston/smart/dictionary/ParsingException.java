package com.coryswainston.smart.dictionary;

/**
 * Exception for when there is an error parsing a JSON response
 */

public class ParsingException extends Exception {

    public ParsingException(Exception e) {
        initCause(e);
    }
}
