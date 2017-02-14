package de.rgse.timecap;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.model.Timeevent;
import de.rgse.timecap.service.UserData;
import de.rgse.timecap.tasks.GetInstantsTask;

public class IntentListActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private InstantListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.incident_list);
        ListView instants = (ListView) findViewById(R.id.content_instants);

        this.adapter = new InstantListAdapter(IntentListActivity.this);
        instants.setAdapter(this.adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();

        String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        try {

            createTask().execute(UserData.getAccount(this).getString("email"), year, month, dayOfMonth);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GetInstantsTask createTask() throws IOException {
        return new GetInstantsTask(this) {
            @Override
            public void done(JsonObject data) {
                int responseCode = data.get("responseCode");
                coordinatorLayout.removeView(findViewById(R.id.loading));

                if (responseCode == 200) {
                    JsonArray array = data.get("data");
                    adapter.setData(array.getTimeevents());

                } else if (responseCode == 204) {
                    Toast.makeText(IntentListActivity.this, "Es wurden noch keine Zeiten erfasst", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void fail(Integer responseCode, JsonObject data) {
                coordinatorLayout.removeView(findViewById(R.id.loading));
                ErrorDialog.show(data, IntentListActivity.this);
            }
        };
    }
}
