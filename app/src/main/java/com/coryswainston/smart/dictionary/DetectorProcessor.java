package com.coryswainston.smart.dictionary;

import android.graphics.Rect;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Processes detected text
 */

public class DetectorProcessor implements Detector.Processor<TextBlock> {

    OnProcessedListener listener;

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        SparseArray<TextBlock> textBlocks = detections.getDetectedItems();

        Map<String, Rect> words = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < textBlocks.size(); i++) {
            sb.append(textBlocks.valueAt(i).getValue());
            List<? extends Text> lines = textBlocks.valueAt(i).getComponents();
            for (int j = 0; j < lines.size(); j++) {
                List<? extends Text> wordList = lines.get(j).getComponents();
                for (int k = 0; k < wordList.size(); k++) {
                    words.put(wordList.get(k).getValue(), wordList.get(k).getBoundingBox());
                }
            }
        }

        listener.onProcessed(words, sb.toString());
    }

    public DetectorProcessor withListener(OnProcessedListener listener) {
        this.listener = listener;
        return this;
    }
}
