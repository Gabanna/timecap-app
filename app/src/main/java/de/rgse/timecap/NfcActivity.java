package de.rgse.timecap;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.UUID;

import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.tasks.PostInstantTask;

public class NfcActivity extends AppCompatActivity {

    private static final String TAG = NfcActivity.class.getSimpleName();

    private String intentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (null == intentID || !intentID.equals(intent.getExtras().getString("id"))) {
            onNewIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            intentID = UUID.randomUUID().toString();
            intent.getExtras().putString("id", intentID);
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (null != rawMessages) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }

                NdefRecord[] records = messages[0].getRecords();
                String payload = new String(records[0].getPayload());

                JsonObject payloadData = new JsonObject(payload);
                String locationId = payloadData.get("locationId");
                String userId = "testuser";
                PostRawData postRawData = new PostRawData(userId, locationId);

                PostInstantTask postInstantTask = new PostInstantTask() {
                    @Override
                    public void onResponse(JsonObject jsonObject) {
                        updateTextview(jsonObject);
                    }
                };
                postInstantTask.execute(postRawData);
            }
        }
    }

    private void updateTextview(JsonObject json) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_nfc);
        TextView conclusion = new TextView(this);

        if (json.getInt("responseCode") == 200) {
            JsonObject data = json.get("data");
            conclusion.setText(data.getString("locationId"));
            layout.addView(conclusion);

            TextView userIdView = new TextView(this);
            userIdView.setText(data.getString("userId"));
            layout.addView(userIdView);

            TextView timeView = new TextView(this);
            timeView.setText(data.getString("instant"));
            layout.addView(timeView);

        } else {
            ErrorDialog.show(json, NfcActivity.this);
        }

    }

}
