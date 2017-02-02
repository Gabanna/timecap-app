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
import java.text.SimpleDateFormat;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.service.IOUtil;
import de.rgse.timecap.service.JwtService;
import de.rgse.timecap.service.TimecapProperties;

public abstract class GetInstantsTask extends AbstractRestTask<String> {

    private static final String TAG = GetInstantsTask.class.getSimpleName();

    private final JwtService jwtService;
    private final String urlString;

    public GetInstantsTask() throws IOException {
        jwtService = new JwtService();
        urlString = String.format("%s/time-events", TimecapProperties.readProperty("rest.baseUrl"));
    }

    @Override
    protected JsonObject doInBackground(String... params) {
        JsonObject result = null;

        HttpURLConnection connection = null;
        try {
            String userId = params[0];
            String url = String.format("%s?userId=%s", urlString, userId);

            if (params.length > 1) {
                String year = params[1];
                url += "&year=" + year;
            }

            if (params.length > 2) {
                String month = params[2];
                url += "&month=" + month;
            }

            if (params.length > 3) {
                String day = params[3];
                url += "&day=" + day;
            }

            connection = (HttpURLConnection) new URL(url).openConnection();
            connection = sendRequest(connection, userId);
            result = getResponse(connection);

        } catch (Exception e) {
            result = new JsonObject();
            result
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
}
