package de.rgse.timecap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.IOException;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.tasks.GetInstantsTask;

public class IntentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_list);

        final ListView listview = (ListView) findViewById(R.id.listview);

        try {
            new GetInstantsTask() {
                @Override
                public void onResponse(JsonObject json) {
                    int responseCode = json.get("responseCode");

                    if (responseCode == 200) {
                        JsonArray array = json.get("data");
                        listview.setAdapter(new ArrayAdapter<Object>(IntentListActivity.this, R.layout.content_instants, array.toArray()));
                    } else if(responseCode == 204) {
                        JsonArray array = new JsonArray();
                        JsonObject object = new JsonObject();
                        object.set("message", "Es konnte kein Eintrag gefunden werden");
                        array.addItem(object);
                        listview.setAdapter(new ArrayAdapter<Object>(IntentListActivity.this, R.layout.content_instants, array.toArray()));

                    } else {
                        String message = json.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(IntentListActivity.this);
                        builder.setTitle(message).setMessage(json.toString()).show();
                    }
                }
            }.execute("testuser");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
