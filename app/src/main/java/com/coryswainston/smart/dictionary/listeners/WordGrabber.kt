package com.coryswainston.smart.dictionary.listeners

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import java.util.*
import kotlin.math.abs

/**
 * A touch listener to retrieve individual words from a textView.
 */
class WordGrabber(private val callback: (String) -> Unit) : OnTouchListener {
    private var downY: Float? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.y
                return false
            }
            MotionEvent.ACTION_UP -> {
                val yDiff = abs(downY!! - event.y)
                if (yDiff < 100) {
                    val tv = v as TextView
                    getWordAndExecuteCallback(tv, event)
                }
                downY = null
                return true
            }
        }
        return false
    }

    private fun getWordAndExecuteCallback(tv: TextView, event: MotionEvent) {
        val offset = tv.getOffsetForPosition(event.x, event.y)
        val text = tv.text.toString()
        if (offset >= text.length) {
            return
        }
        var endIndex = offset
        var startIndex = offset
        run {
            var i = offset
            while (i < text.length && Character.isLetterOrDigit(text[i])) {
                endIndex++
                i++
            }
        }
        var i = offset
        while (i > 0 && Character.isLetterOrDigit(text[i - 1])) {
            startIndex--
            i--
        }
        val word = text.subSequence(startIndex, endIndex).toString().toLowerCase(Locale.getDefault())
        val ss = SpannableString(text)
        ss.setSpan(BackgroundColorSpan(Color.WHITE), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(Color.rgb(40, 150, 255)),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = ss
        callback(word)
    }
}