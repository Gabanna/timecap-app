package de.rgse.timecap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import de.rgse.timecap.fassade.JsonObject;

public class ErrorDialog {

    private ErrorDialog() {
    }

    public static void showResponseError(final JsonObject json, final Context context) {
        String message = context.getResources().getString(R.string.server_connection_failed);
        int responseCode = json.get("responseCode");
        String error = json.get("error");

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder
                .setTitle(context.getResources().getString(R.string.server_connection_failed))
                .setMessage(String.format("%s\n\nhttp %s: %s", message, responseCode, error))
                .show();
    }

    public static void show(final JsonObject json, final Context context) {
        String message = context.getResources().getString(R.string.server_connection_failed);
        String detail = context.getResources().getString(R.string.server_connection_failed_detail);
        String error = json.get("error");

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder
                .setTitle(message)
                .setMessage(String.format("%s\n\n%s", detail, error))
                .show();
    }


    public static void show(Throwable throwable, final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder
                .setTitle("Es ist ein Fehler aufgetreten")
                .setMessage(throwable.getMessage())
                .show();
    }
}
