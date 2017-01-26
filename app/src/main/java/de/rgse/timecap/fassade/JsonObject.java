package de.rgse.timecap.fassade;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.rgse.timecap.fassade.exception.JsonException;
import de.rgse.timecap.service.IOUtil;

public class JsonObject {

    private final JSONObject core;

    public JsonObject() {
        this.core = new JSONObject();
    }

    public JsonObject(InputStream inputStream) {
        try {
            String data = IOUtil.readInputStream(inputStream);
            this.core = new JSONObject(data);

        } catch (IOException | JSONException e) {
            throw new JsonException(e);
        }
    }

    public JsonObject(JSONObject data) {
        this.core = data;
    }

    public JsonObject(String data) {
        try {
            this.core = new JSONObject(data);
        } catch (JSONException e) {
            throw new JsonException(e);
        }
    }

    public JsonObject set(String name, Object value) throws JsonException {
        if (null != name && null != value) {
            try {
                core.put(name, value);
            } catch (JSONException e) {
                throw new JsonException(e);
            }
        }
        return this;
    }

    public Integer getInt(String name) {
        return get(name);
    }

    public String getString(String name) {
        return get(name);
    }

    public <T> T get(String name) {
        T result = null;
        try {
            result = (T) core.get(name);
        } catch (JSONException e) {
        }

        return result;
    }

    @Override
    public String toString() {
        return core.toString();
    }
}
