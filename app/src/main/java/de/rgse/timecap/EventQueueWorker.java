package de.rgse.timecap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.model.EventQueue;
import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.service.UserData;
import de.rgse.timecap.tasks.PostInstantTask;

public class EventQueueWorker extends BroadcastReceiver {

    private static final int ID = 815;

    public static int getId() {
        return ID;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null) {
            final EventQueue eventQueue = UserData.getEventQueue(context);

            if (!eventQueue.isEmpty()) {
                final int total = eventQueue.size();
                final List<PostRawData> completed = new ArrayList<>();
                final List<PostRawData> failed = new ArrayList<>();

                while (!eventQueue.isEmpty()) {
                    final PostRawData postRawData = eventQueue.pop();
                    new PostInstantTask() {
                        @Override
                        public void done(JsonObject data) {
                            completed.add(postRawData);
                        }

                        @Override
                        public void fail(Integer responseCode, JsonObject data) {
                            failed.add(postRawData);
                        }

                        @Override
                        public void always() {
                            if (completed.size() + failed.size() == total && !completed.isEmpty()) {
                                sendNotification(context, completed, failed);
                            }
                        }
                    }.execute(postRawData);
                }

                eventQueue.pushAll(failed);
                UserData.setEventQueue(context, eventQueue);
            }
        }
    }

    private void sendNotification(Context context, List<?> completed, List<?> failed) {
        String timePluralPostfix = completed.size() > 1 ? "en" : "";
        String verbPluralPostfix = completed.size() > 1 ? "n" : "";

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.timecap_logo)
                .setContentTitle("Timecap")
                .setContentText(String.format("%s Zeit%s wurde%s nachgetragen", completed.size(), timePluralPostfix, verbPluralPostfix));

        Intent result = new Intent(context, MainActivity.class);
        final TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(result);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID, builder.build());
    }
}
