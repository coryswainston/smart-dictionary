package com.coryswainston.smart.dictionary;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A class to simplify reading JSON.
 */

public class ParsableJson<T> implements Iterable<ParsableJson<Object>> {

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
    public ParsableJson<Object> getObject(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        Object resultObject = jsonMap.get(key);

        return new ParsableJson<>(resultObject);
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<Object> getObject(int index) {
        List<Object> jsonList = (List<Object>) jsonObject;
        Object resultObject = jsonList.get(index);

        return new ParsableJson<>(resultObject);
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<List<Object>> getList(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        List<Object> resultList = (List<Object>) jsonMap.get(key);

        return new ParsableJson<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public ParsableJson<List<Object>> getList(int index) {
        List<Object> jsonList = (List<Object>) jsonObject;
        List<Object> resultList = (List<Object>) jsonList.get(index);

        return new ParsableJson<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public Object getObjectFromKey(String key) {
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
        return jsonMap.get(key);
    }

    public T get() {
        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    public <T1, T2> Map<T1, T2> getAsMap(Class<T1> t1, Class<T2> t2) {
        return (Map<T1, T2>)jsonObject;
    }

    @SuppressWarnings("unchecked")
    public <T2> T2 getAsObject(Class<T2> t2) {
        return (T2) jsonObject;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getAsList() {
        return getAsList(Object.class);
    }

    @SuppressWarnings("unchecked")
    public <T2> List<T2> getAsList(Class<T2> t2) {
        return (List<T2>) jsonObject;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Iterator<ParsableJson<Object>> iterator() {
        return new Iterator<ParsableJson<Object>>() {

            private Iterator<Object> it = ((List<Object>)jsonObject).iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public ParsableJson<Object> next() {
                return new ParsableJson<>(it.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("This iterator does not support removal.");
            }
        };
    }
}
