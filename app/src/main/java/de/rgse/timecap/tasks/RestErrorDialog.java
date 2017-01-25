package de.rgse.timecap.tasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.json.JSONObject;

import de.rgse.timecap.R;

/**
 * Created by absolem on 25.01.17.
 */

public class RestErrorDialog extends DialogFragment {

    private JSONObject data;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = "Hallo World";
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
