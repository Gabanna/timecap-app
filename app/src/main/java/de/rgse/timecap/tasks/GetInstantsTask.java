package de.rgse.timecap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.service.IOUtil;
import de.rgse.timecap.service.JwtService;
import de.rgse.timecap.service.TimecapProperties;

public abstract class GetInstantsTask extends AsyncTask<String, Void, JsonObject> {

    private static final String TAG = GetInstantsTask.class.getSimpleName();

    private final JwtService jwtService;
    private final String urlString;

    public GetInstantsTask() throws IOException {
        jwtService = new JwtService();
        urlString = TimecapProperties.readProperty("rest.getInstants");
    }

    public abstract void onResponse(JsonObject json);

    @Override
    protected JsonObject doInBackground(String... params) {
        JsonObject result = null;

        HttpURLConnection connection = null;
        try {
            String userId = params[0];
            connection = (HttpURLConnection) new URL(String.format("%s?userId=%s", urlString, userId)).openConnection();
            connection = sendRequest(connection, userId);
            result = getResponse(connection);

        } catch (Exception e) {
            result = new JsonObject();
            result.set("responseCode", 400)
                    .set("message", "unable to connect to server")
                    .set("error", e.getMessage());

            Log.e(TAG, "unable to create connection", e);

        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(JsonObject json) {
        super.onPostExecute(json);
        onResponse(json);
    }

    private HttpURLConnection sendRequest(HttpURLConnection connection, String userId) throws IOException, JSONException {
        String jwt = jwtService.generateJwt(userId);

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", jwt);
        connection.setConnectTimeout(3 * 1000);

        return connection;
    }

    private JsonObject getResponse(HttpURLConnection connection) throws IOException {
        JsonObject result = new JsonObject();

        int responseCode = connection.getResponseCode();
        result.set("responseCode", responseCode);

        if (responseCode == 200) {
            result.set("data", new JsonArray(connection.getInputStream()));

        } else if (responseCode >= 300) {
            result.set("message", connection.getResponseMessage());

            String content = IOUtil.readInputStream(connection.getErrorStream());

            if (IOUtil.stringHasContent(content)) {
                result.set("error", content);
            }
        }

        return result;
    }

    private boolean isSuccessfull(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }
}
