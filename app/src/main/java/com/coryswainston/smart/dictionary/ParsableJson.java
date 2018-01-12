package com.coryswainston.smart.dictionary;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A class to simplify reading JSON.
 */

public class ParsableJson<T> {

    private T jsonObject;

    public ParsableJson(String json) throws IOException {
        this.jsonObject = new ObjectMapper().readValue(json, new TypeReference<T>() {
        });
    }

    public ParsableJson(T jsonObject) {
        this.jsonObject = jsonObject;
        Log.d("ParsableJson", jsonObject.toString());
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<Map<String, Object>> getObject(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        Map<String, Object> resultObject = (Map<String, Object>) jsonMap.get(key);

        return new ParsableJson<>(resultObject);
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<Map<String, Object>> getObject(int index) {
        List<Map<String, Object>> jsonList = (List<Map<String, Object>>) jsonObject;
        Map<String, Object> resultObject = (Map<String, Object>) jsonList.get(index);

        return new ParsableJson<>(resultObject);
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<List<Map<String, Object>>> getList(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) jsonMap.get(key);

        return new ParsableJson<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<List<Map<String, Object>>> getList(int index) {
        List<Map<String, Object>> jsonList = (List<Map<String, Object>>) jsonObject;
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) jsonList.get(index);

        return new ParsableJson<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getListOfObjects(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        return (List<Object>) jsonMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public Object getObjectFromKey(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        return jsonMap.get(key);
    }

    public T getJson() {
        return jsonObject;
    }
}
