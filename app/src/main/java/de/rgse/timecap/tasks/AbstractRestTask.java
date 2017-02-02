package de.rgse.timecap.tasks;

import android.os.AsyncTask;

import de.rgse.timecap.fassade.JsonObject;

public abstract class AbstractRestTask<P> extends AsyncTask<P, Void, JsonObject> {

    public abstract void done(JsonObject data);

    public abstract void fail(Integer responseCode, JsonObject data);

    protected abstract JsonObject doInBackground(P... params);

    @Override
    protected void onPostExecute(JsonObject json) {
        super.onPostExecute(json);
        Integer responseCode = json.get("responseCode");

        if(responseCode != null && responseCode < 300) {
            done(json);

        } else {
            fail(responseCode, json);
        }

        always();
    }

    public void always() {
    }
}
