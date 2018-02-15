package com.coryswainston.smart.dictionary;

import android.graphics.Rect;

import java.util.Map;

/**
 * Get processed word data from processor
 */

public interface OnProcessedListener {
    public void onProcessed(Map<String, Rect> results, String text);
}
