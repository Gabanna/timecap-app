package de.rgse.timecap;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.service.IOUtil;
import de.rgse.timecap.service.LoginService;
import de.rgse.timecap.service.TimecapProperties;
import de.rgse.timecap.service.UserData;
import de.rgse.timecap.tasks.PostInstantTask;

public class NfcActivity extends AppCompatActivity {

    static {
        Locale.setDefault(Locale.GERMANY);
    }

    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd. MMMM HH:mm 'Uhr'", Locale.getDefault());

    private LinearLayout layout;
    private String intentID;
    private TextView user, userLabel, location, locationLabel, time, timeLabel;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!UserData.instance(this).has(UserData.SERVER_URL)) {
            UserData.instance(this).set(UserData.SERVER_URL, TimecapProperties.readProperty("rest.baseUrl"));
        }

        layout = (LinearLayout) findViewById(R.id.content_nfc);

        time = (TextView) findViewById(R.id.time_value);
        timeLabel = (TextView) findViewById(R.id.time_label);
        user = (TextView) findViewById(R.id.user_value);
        userLabel = (TextView) findViewById(R.id.user_label);
        location = (TextView) findViewById(R.id.location_value);
        locationLabel = (TextView) findViewById(R.id.location_label);

        Intent intent = getIntent();
        if (null == intentID || !intentID.equals(intent.getExtras().getString("id"))) {
            onNewIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserData.hasAccount(this)) {
            loginService = new LoginService(this);
            loginService.login();
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
                String userId = UserData.getAccount(this).get("email");
                PostRawData postRawData = new PostRawData(userId, locationId);

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if (connectivityManager.getActiveNetworkInfo() != null) {
                    createTask(postRawData).execute(postRawData);

                } else {
                    layout.removeView(findViewById(R.id.loading));

                    Calendar calendar = Calendar.getInstance();
                    postRawData.setInstant(Calendar.getInstance());
                    findViewById(R.id.main_label).setVisibility(View.VISIBLE);

                    time.setText(DISPLAY_FORMAT.format(calendar.getTime()));
                    timeLabel.setVisibility(View.VISIBLE);
                    user.setText(postRawData.getUserId());
                    userLabel.setVisibility(View.VISIBLE);
                    location.setText(postRawData.getLocationId());

                    locationLabel.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Es ist keine Internetverbindung vorhanden.\nDie Zeit wird später eingetragen.", Toast.LENGTH_LONG).show();

                    UserData.queueEvent(this, postRawData);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginService.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                JsonObject jsonObject = JsonObject.createUserData(account);
                UserData.instance(this).set("account", jsonObject.toString());

                onNewIntent(getIntent());
            } else {
                if (result.getStatus().getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                    ErrorDialog.show(new JsonObject(), this);
                }
            }
        }
    }

    private PostInstantTask createTask(final PostRawData postRawData) {
        return new PostInstantTask(this) {
            @Override
            public void done(JsonObject json) {
                layout.removeView(findViewById(R.id.loading));

                findViewById(R.id.main_label).setVisibility(View.VISIBLE);

                JsonObject data = json.get("data");
                String formatedTime = null;
                try {
                    formatedTime = DISPLAY_FORMAT.format(IOUtil.formatDate(data.getString("instant")));

                    time.setText(formatedTime);
                    timeLabel.setVisibility(View.VISIBLE);
                    user.setText(data.getString("userId"));
                    userLabel.setVisibility(View.VISIBLE);
                    location.setText(data.getString("locationId"));
                    locationLabel.setVisibility(View.VISIBLE);

                } catch (ParseException e) {
                    ErrorDialog.show(e, NfcActivity.this);
                }
            }

            @Override
            public void fail(Integer responseCode, JsonObject data) {
                layout.removeView(findViewById(R.id.loading));
                postRawData.setInstant(Calendar.getInstance(Locale.GERMANY));
                UserData.queueEvent(NfcActivity.this, postRawData);
                ErrorDialog.show(data, NfcActivity.this);
            }
        };
    }
}
