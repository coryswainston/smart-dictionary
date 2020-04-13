package com.coryswainston.smart.dictionary.helpers.parsing

/**
 * Exception for when there is an error parsing a JSON response
 */
class ParsingException(e: Exception?) : Exception() {
    init {
        initCause(e)
    }
}