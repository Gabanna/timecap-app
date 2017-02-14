package de.rgse.timecap.service;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

public class LoginService {

    public static final int RC_SIGN_IN = 9001;

    private final GoogleApiClient googleApiClient;
    private AppCompatActivity activity;

    public LoginService(AppCompatActivity activity) {
        System.out.println("calling loginservice");
        this.activity = activity;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(activity).enableAutoManage(activity, 1, onConnectionFailed()).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    public void login() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void disconnectFromGoogleApi() {
        if(googleApiClient != null) {
            googleApiClient.stopAutoManage(activity);
        }
    }

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailed() {
        return new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(activity, connectionResult.getErrorMessage(), Toast.LENGTH_LONG);
            }
        };
    }
}
