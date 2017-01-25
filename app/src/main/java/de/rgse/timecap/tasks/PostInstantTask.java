package de.rgse.timecap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.service.JwtService;
import de.rgse.timecap.service.TimecapProperties;

public abstract class PostInstantTask extends AsyncTask<PostRawData, Void, JSONObject> {

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

    public abstract void onResponse(JSONObject JSONObject);

    @Override
    protected JSONObject doInBackground(PostRawData[] params) {
        JSONObject result = null;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection = sendRequest(connection, params[0]);
            result = getResponse(connection);

        } catch (Exception e) {
            try {
                result = new JSONObject();
                result.put("responseCode", 400);
                result.put("message", "unable to connect to server");
                result.put("error", e.getMessage());
                Log.e(TAG, "unable to create connection", e);

            } catch (JSONException e1) {
                Log.e(TAG, e1.getMessage());
            }

        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(JSONObject JSONObject) {
        onResponse(JSONObject);
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {
        onResponse(jsonObject);
    }

    private HttpURLConnection sendRequest(HttpURLConnection connection, PostRawData postRawData) throws IOException, JSONException {
        String jwt = jwtService.generateJwt(postRawData.getUserId());

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", jwt);

        connection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(postRawData.asJson().toString());
        dataOutputStream.flush();
        dataOutputStream.close();

        return connection;
    }

    private JSONObject getResponse(HttpURLConnection connection) throws IOException, JSONException {
        JSONObject result;

        int responseCode = connection.getResponseCode();
        result = new JSONObject();
        result.put("responseCode", responseCode);

        if (isSuccessfull(responseCode)) {
            String data = readData(connection.getInputStream());
            result.put("data", new JSONObject(data));

        } else {
            String content = readData(connection.getErrorStream());

            result.put("message", connection.getResponseMessage());

            if (content != null && !content.isEmpty()) {
                result.put("error", content);
            }
        }

        return result;
    }

    private boolean isSuccessfull(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }

    private String readData(InputStream inputStream) throws IOException {
        BufferedInputStream reader = new BufferedInputStream(inputStream);

        byte[] contentBytes = new byte[1024];
        int bytesRead;
        String result = "";

        while ((bytesRead = reader.read(contentBytes)) != -1) {
            result += new String(contentBytes, 0, bytesRead);
        }

        return result;
    }
}
