package com.coryswainston.smart.dictionary;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * A touch listener to retrieve individual words from a textView.
 */

public class WordGrabber implements View.OnTouchListener {
    private TextView tv;

    public WordGrabber(TextView placeResultIn) {
        tv = placeResultIn;
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
        for (int i = offset; i < text.length() && !breakChars.contains(text.charAt(i)); i++) {
            endIndex++;
        }
        for (int i = offset; i > 1 && !breakChars.contains(text.charAt(i)); i--) {
            startIndex--;
        }

        tv.setText(text.subSequence(startIndex, endIndex).toString());

        return false;
    }
}
