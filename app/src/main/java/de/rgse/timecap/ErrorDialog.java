package de.rgse.timecap;

import android.app.AlertDialog;
import android.content.Context;

import de.rgse.timecap.fassade.JsonObject;

public class ErrorDialog {

    private ErrorDialog() {
    }

    public static void show(JsonObject json, Context context) {
        String message = json.getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(message).setMessage(json.toString()).show();
    }
}
