package de.rgse.timecap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.tasks.PostInstantTask;
import de.rgse.timecap.tasks.RestErrorDialog;
import de.rgse.timecap.tasks.TimecapTaskException;

public class NfcActivity extends Activity {

    private static final String TAG = NfcActivity.class.getSimpleName();

    private String intentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

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

                try {
                    JSONObject payloadData = new JSONObject(payload);
                    String locationId = payloadData.getString("locationId");
                    String userId = "testuser";
                    PostRawData postRawData = new PostRawData(userId, locationId);

                    PostInstantTask postInstantTask = new PostInstantTask() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                updateTextview(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    postInstantTask.execute(postRawData);

                } catch (TimecapTaskException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateTextview(JSONObject json) throws JSONException {
        LinearLayout layout = (LinearLayout) findViewById(R.id.result);
        TextView conclusion = new TextView(this);

        if (json.getInt("responseCode") == 200) {
            JSONObject data = json.getJSONObject("data");
            conclusion.setText(data.getString("locationId"));
            layout.addView(conclusion);

            TextView userIdView = new TextView(this);
            userIdView.setText(data.getString("userId"));
            layout.addView(userIdView);

            TextView timeView = new TextView(this);
            timeView.setText(data.getString("instant"));
            layout.addView(timeView);

        } else {
            String message = json.getString("message");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(message).setMessage(json.toString()).show();
        }

    }

}
