package de.rgse.timecap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.Locale;

import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.service.LoginService;
import de.rgse.timecap.service.TimecapProperties;
import de.rgse.timecap.service.UserData;
import de.rgse.timecap.tasks.DownloadImageTask;

public class MainActivity extends AppCompatActivity {

    static {
        Locale.setDefault(Locale.GERMANY);
    }

    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.btnShowInstants);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IntentListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_user) {
            startActivity(new Intent(this, UserActivity.class));
            return false;
        }

        return super.onOptionsItemSelected(item);
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
            } else {
                if (result.getStatus().getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                    ErrorDialog.show(new JsonObject(), this);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserData.hasAccount(this)) {
            loginService = new LoginService(this);
            loginService.login();
        } else {
            String photoUrl = UserData.getAccount(this).get("photoUrl");
            if(photoUrl != null) {
                new DownloadImageTask(this).execute(photoUrl);

            }

            new EventQueueWorker().onReceive(this, getIntent());
        }
    }
}
