package de.rgse.timecap.fassade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.rgse.timecap.fassade.exception.JsonException;
import de.rgse.timecap.model.Timeevent;
import de.rgse.timecap.service.IOUtil;

public class JsonArray implements Iterable<JsonObject> {

    private JSONArray core;

    public JsonArray() {
        core = new JSONArray();
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
            this.core = null == data ? new JSONArray() : new JSONArray(data);
        } catch (JSONException e) {
            throw new JsonException(e);
        }
    }

    public JsonArray add(JsonObject object) {
        core.put(object);
        return this;
    }

    public JsonObject get(int index) throws JsonException {
        JsonObject result;
        try {
            String jsonObject = core.getString(index);
            result = new JsonObject(jsonObject);

        } catch (JSONException e) {
            throw new JsonException(e);
        }

        return result;
    }

    public List<Timeevent> getTimeevents() {
        List<Timeevent> timeevents = null;
        try {
            timeevents = new ArrayList<>();
            for (JsonObject jsonObject : this) {
                timeevents.add(Timeevent.fromJson(jsonObject));
            }
        } catch (JsonException e) {
            e.printStackTrace();
        }

        return timeevents;
    }

    @Override
    public String toString() {
        return core.toString();
    }

    @Override
    public Iterator<JsonObject> iterator() {
        return new Iterator<JsonObject>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return index < core.length() - 1;
            }

            @Override
            public JsonObject next() {
                JsonObject result;
                try {
                    result = new JsonObject(core.getString(++index));
                } catch (JSONException e) {
                    throw new JsonException(e);
                }
                return result;
            }
        };
    }

    public int size() {
        return core.length();
    }

    public void remove(int i) {
        core.remove(i);
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
