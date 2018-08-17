package com.coryswainston.smart.dictionary.listeners;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * A touch listener to retrieve individual words from a textView.
 */

public class WordGrabber implements View.OnTouchListener {

    private static final String TAG = "WordGrabber";

    private Callback callback;
    private Float downY;

    public WordGrabber(Callback callback) {
        this.callback = callback;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        int eventType = event.getActionMasked();

        switch (eventType) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                return false;
            case MotionEvent.ACTION_UP:
                float yDiff = Math.abs(downY - event.getY());
                if (yDiff < 100) {
                    TextView tv = (TextView)v;
                    getWordAndExecuteCallback(tv, event);
                }
                downY = null;
                return true;
        }
        return false;
    }

    private void getWordAndExecuteCallback(TextView tv, MotionEvent event) {
        int offset = tv.getOffsetForPosition(event.getX(), event.getY());
        final String text = tv.getText().toString();
        if (offset >= text.length()) {
            return;
        }

        int endIndex   = offset;
        int startIndex = offset;
        for (int i = offset; i < text.length() && Character.isLetterOrDigit(text.charAt(i)); i++) {
            endIndex++;
        }
        for (int i = offset; i > 0 && Character.isLetterOrDigit(text.charAt(i - 1)); i--) {
            startIndex--;
        }
        String word = text.subSequence(startIndex, endIndex).toString().toLowerCase();
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new BackgroundColorSpan(Color.WHITE), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.rgb(40, 150, 255)),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
        callback.callback(word);
    }

    public interface Callback {
        void callback(String text);
    }
}
