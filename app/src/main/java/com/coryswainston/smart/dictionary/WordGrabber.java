package com.coryswainston.smart.dictionary;

import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * A touch listener to retrieve individual words from a textView.
 */

public class WordGrabber implements View.OnTouchListener {
    private AsyncTask<String, Integer, String> task;

    public WordGrabber(AsyncTask<String, Integer, String> sendResultTo) {
        task = sendResultTo;
    }

    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        TextView view = (TextView)v;
        int offset = view.getOffsetForPosition(event.getX(), event.getY());
        CharSequence text = view.getText();
        if (offset >= text.length()) {
            return false;
        }

        List<Character> breakChars = Arrays.asList(' ', '\n');

        int endIndex   = offset;
        int startIndex = offset;
        for (int i = offset; i < text.length() && Character.isLetterOrDigit(text.charAt(i)); i++) {
            endIndex++;
        }
        for (int i = offset; i > 0 && Character.isLetterOrDigit(text.charAt(i - 1)); i--) {
            startIndex--;
        }

        if (task.getStatus() == AsyncTask.Status.PENDING) {
            task.execute(text.subSequence(startIndex, endIndex).toString().toLowerCase());
        }

        return false;
    }
}
