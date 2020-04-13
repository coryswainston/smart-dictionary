package com.coryswainston.smart.dictionary.services

import android.graphics.Rect
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.text.Text
import com.google.android.gms.vision.text.TextBlock
import java.util.*

/**
 * Processes detected text
 */
class DetectorProcessor : Detector.Processor<TextBlock> {
    private lateinit var callback: (List<Text>?, String?) -> Unit

    override fun release() {
        // empty
    }

    override fun receiveDetections(detections: Detections<TextBlock>) {
        val textBlocks = detections.detectedItems
        val wordBlocks: MutableList<Text> = ArrayList()
        val words: MutableMap<String, Rect> = HashMap()
        val sb = StringBuilder()
        for (i in 0 until textBlocks.size()) {
            sb.append(textBlocks.valueAt(i).value)
            val lines = textBlocks.valueAt(i).components
            for (j in lines.indices) {
                val wordList = lines[j].components
                for (k in wordList.indices) {
                    words[wordList[k].value] = wordList[k].boundingBox
                    wordBlocks.add(wordList[k])
                }
            }
        }
        callback(wordBlocks, sb.toString())
    }

    fun withCallback(callback: (List<Text>?, String?) -> Unit): DetectorProcessor {
        this.callback = callback
        return this
    }
}