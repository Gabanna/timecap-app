package de.rgse.timecap.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.view.menu.MenuView;
import android.util.Log;

import java.io.InputStream;

import de.rgse.timecap.R;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private MenuView.ItemView itemView;
    private Activity activity;

    public DownloadImageTask(Activity activity) {
        this.activity = activity;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(null != result) {
            MenuView.ItemView itemView = (MenuView.ItemView) activity.findViewById(R.id.action_user);
            itemView.setIcon(new BitmapDrawable(activity.getResources(), result));
        }
    }
}
