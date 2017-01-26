package de.rgse.timecap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.service.IOUtil;
import de.rgse.timecap.service.JwtService;
import de.rgse.timecap.service.TimecapProperties;

public abstract class PostInstantTask extends AsyncTask<PostRawData, Void, JsonObject> {

    private static final String TAG = "PostInstantTask";

    private final String urlString;
    private final JwtService jwtService;

    public PostInstantTask() throws TimecapTaskException {
        try {
            urlString = TimecapProperties.readProperty("rest.postInstant");
            jwtService = new JwtService();

        } catch (IOException e) {
            throw new TimecapTaskException(e);
        }
    }

    public abstract void onResponse(JsonObject json);

    @Override
    protected JsonObject doInBackground(PostRawData[] params) {
        JsonObject result = null;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection = sendRequest(connection, params[0]);
            result = getResponse(connection);

        } catch (Exception e) {
            result = new JsonObject()
                    .set("responseCode", 400)
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
        onResponse(json);
    }

    @Override
    protected void onCancelled(JsonObject jsonObject) {
        onResponse(jsonObject);
    }

    private HttpURLConnection sendRequest(HttpURLConnection connection, PostRawData postRawData) throws IOException, JSONException {
        String jwt = jwtService.generateJwt(postRawData.getUserId());

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", jwt);
        connection.setConnectTimeout(3 * 1000);

        connection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(postRawData.asJson().toString());
        dataOutputStream.flush();
        dataOutputStream.close();

        return connection;
    }

    private JsonObject getResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        JsonObject result = new JsonObject().set("responseCode", responseCode);

        if (isSuccessfull(responseCode)) {
            result.set("data", new JsonObject(connection.getInputStream()));

        } else {
            String content = IOUtil.readInputStream(connection.getErrorStream());
            result.set("message", connection.getResponseMessage());

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
