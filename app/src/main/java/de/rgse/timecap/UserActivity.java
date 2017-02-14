package de.rgse.timecap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.rgse.timecap.service.UserData;

public class UserActivity extends AppCompatActivity {

    private Button button;
    private EditText serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.userName);
        textView.setText(UserData.getAccount(this).getString("email"));

        serverUrl = (EditText) findViewById(R.id.serverUrl);
        serverUrl.setText(UserData.instance(this).get(UserData.SERVER_URL));

        button = (Button) findViewById(R.id.btnServerUrl);
        button.setOnClickListener(onChange());
    }

    private View.OnClickListener onChange() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData.instance(UserActivity.this).set(UserData.SERVER_URL, serverUrl.getText().toString());
                Toast.makeText(UserActivity.this, "Die Einstellungen wurden angepasst", Toast.LENGTH_LONG).show();
            }
        };
    }

}
