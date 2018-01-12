package com.coryswainston.smart.dictionary;

/**
 * An interface to separate an AsyncTask from the activity it's called in.
 */

public interface OnCompleteListener {
    public void onComplete(String result);
}
