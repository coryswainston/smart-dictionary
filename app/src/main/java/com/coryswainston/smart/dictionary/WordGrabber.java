package com.coryswainston.smart.dictionary;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * A touch listener to retrieve individual words from a textView.
 */

public class WordGrabber implements View.OnTouchListener {
    private Callback callback;

    private MotionEvent down;

    public WordGrabber(Callback callback) {
        this.callback = callback;
    }

    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && down != null) {
//            down = event;
            return false;
        }
        down = event;
//        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
//            MotionEvent temp = down;
//            down = null;
//            if (Math.abs(temp.getX() - temp.getX()) > 10 ||
//                    Math.abs(temp.getY() - event.getY()) > 10) {
//                return false;
//            }
//        }

        TextView view = (TextView)v;
        int offset = view.getOffsetForPosition(event.getX(), event.getY());
        CharSequence text = view.getText();
        if (offset >= text.length()) {
            return false;
        }

        int endIndex   = offset;
        int startIndex = offset;
        for (int i = offset; i < text.length() && Character.isLetterOrDigit(text.charAt(i)); i++) {
            endIndex++;
        }
        for (int i = offset; i > 0 && Character.isLetterOrDigit(text.charAt(i - 1)); i--) {
            startIndex--;
        }

        callback.callback(text.subSequence(startIndex, endIndex).toString().toLowerCase());

        return false;
    }

    public interface Callback {
        void callback(String text);
    }
}
