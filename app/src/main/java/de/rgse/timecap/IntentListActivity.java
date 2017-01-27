package de.rgse.timecap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridLayout;
import android.widget.TextView;

import java.io.IOException;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.tasks.GetInstantsTask;

public class IntentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {

            new GetInstantsTask() {
                @Override
                public void onResponse(JsonObject json) {
                    int responseCode = json.get("responseCode");

                    if (responseCode == 200) {
                        JsonArray array = json.get("data");

                        setContent(array);
                    } else if (responseCode == 204) {
                        JsonObject object = new JsonObject();
                        object.set("message", "Es konnte kein Eintrag gefunden werden");
                        setContent(object);

                    } else {
                        ErrorDialog.show(json, IntentListActivity.this);
                    }
                }
            }.execute("USER1");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setContent(JsonArray jsonArray) {
        GridLayout layout = (GridLayout) findViewById(R.id.content_instants);
        for (JsonObject jsonObject : jsonArray) {
            addTextView(jsonObject.toString(), layout);
        }
    }


    private void setContent(JsonObject jsonObject) {
        GridLayout layout = (GridLayout) findViewById(R.id.content_instants);
        addTextView(jsonObject.toString(), layout);
    }

    private void addTextView(String text, GridLayout layout) {
        TextView textView = new TextView(this);
        textView.setText(text);
        layout.addView(textView);
    }
}
