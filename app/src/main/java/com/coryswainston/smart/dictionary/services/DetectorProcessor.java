package com.coryswainston.smart.dictionary.services;

import android.graphics.Rect;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes detected text
 */

public class DetectorProcessor implements Detector.Processor<TextBlock> {

    private Callback callback;

    @Override
    public void release() {
        // empty
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        SparseArray<TextBlock> textBlocks = detections.getDetectedItems();

        List<Text> wordBlocks = new ArrayList<>();

        Map<String, Rect> words = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < textBlocks.size(); i++) {
            sb.append(textBlocks.valueAt(i).getValue());
            List<? extends Text> lines = textBlocks.valueAt(i).getComponents();
            for (int j = 0; j < lines.size(); j++) {
                List<? extends Text> wordList = lines.get(j).getComponents();
                for (int k = 0; k < wordList.size(); k++) {
                    words.put(wordList.get(k).getValue(), wordList.get(k).getBoundingBox());
                    wordBlocks.add(wordList.get(k));
                }
            }
        }

        callback.callback(wordBlocks, sb.toString());
    }

    public DetectorProcessor withCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public interface Callback {
        void callback(List<Text> blocks, String detectedText);
    }
}
