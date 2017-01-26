package de.rgse.timecap.fassade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.Iterator;

import de.rgse.timecap.fassade.exception.JsonException;
import de.rgse.timecap.service.IOUtil;

public class JsonArray extends AbstractList<JsonObject> {

    private final JSONArray core;

    public JsonArray() {
        this.core = new JSONArray();
    }

    public JsonArray(InputStream inputStream) {
        try {
            String data = IOUtil.readInputStream(inputStream);
            core = new JSONArray(data);

        } catch (IOException | JSONException e) {
            throw new JsonException(e);
        }
    }

    public JsonArray(String data) throws JsonException {
        try {
            this.core = new JSONArray(data);
        } catch (JSONException e) {
            throw new JsonException(e);
        }
    }

    public JsonArray(JSONArray data) {
        this.core = data;
    }

    public JsonArray addItem(Object object){
        core.put(object);
        return this;
    }

    @Override
    public JsonObject get(int index) throws JsonException {
        JsonObject result = null;
        try {
            JSONObject jsonObject = core.getJSONObject(index);
            result = new JsonObject(jsonObject);

        } catch (JSONException e) {
            throw new JsonException(e);
        }

        return result;
    }

    @Override
    public int size() {
        return core.length();
    }

    @Override
    public String toString() {
        return core.toString();
    }

    @Override
    public Object[] toArray() {
        try {
            final JSONArray jsonArray = new JSONArray(core.toString());
            Object[] result = new Object[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                result[i] = jsonArray.get(i);
            }

            return result;
        } catch (JSONException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public Iterator<JsonObject> iterator() {
        try {
            final JSONArray jsonArray = new JSONArray(core.toString());
            return new Iterator<JsonObject>() {
                int index = -1;

                @Override
                public boolean hasNext() {
                    return index < jsonArray.length();
                }

                @Override
                public JsonObject next() {
                    JsonObject result = null;
                    try {
                        result = new JsonObject(jsonArray.getJSONObject(++index));
                    } catch (JSONException e) {
                        throw new JsonException(e);
                    }
                    return null;
                }
            };
        } catch (JSONException e) {
            throw new JsonException(e);
        }
    }
}
