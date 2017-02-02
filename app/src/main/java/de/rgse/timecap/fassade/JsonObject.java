package de.rgse.timecap.fassade;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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

    public boolean hasValue(String name) {
        return get(name) != null;
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


    public static JsonObject createUserData(GoogleSignInAccount account) {
        JsonObject result = new JsonObject();
        result.set("email", account.getEmail());
        result.set("photoUrl", account.getPhotoUrl().toString());
        return result;
    }
}
