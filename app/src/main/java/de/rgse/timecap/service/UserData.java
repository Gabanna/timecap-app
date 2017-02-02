package de.rgse.timecap.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.model.EventQueue;
import de.rgse.timecap.model.PostRawData;
import de.rgse.timecap.model.Timeevent;

public class UserData {

    private static final String EVENT_QUEUE = "eventQueue";

    private static UserData instance;

    private SharedPreferences sharedPreferences;

    private UserData(Context activity) {
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("userData", Context.MODE_PRIVATE);
    }

    public static UserData instance(Context context) {
        if (null == instance) {
            instance = new UserData(context);
        }

        return instance;
    }

    public String get(String name) {
        return sharedPreferences.getString(name, null);
    }

    public UserData set(String name, String value) {
        sharedPreferences.edit().putString(name, value).apply();
        return this;
    }

    public UserData remove(String name) {
        sharedPreferences.edit().remove(name).apply();
        return this;
    }

    public static JsonObject getAccount(Context context) {
        return new JsonObject(instance(context).get("account"));
    }

    public static boolean hasAccount(Context context) {
        return instance(context).has("account");
    }

    public static EventQueue getEventQueue(Context context) {
        return new EventQueue(instance(context).get(EVENT_QUEUE));
    }

    public boolean has(String name) {
        return get(name) != null;
    }

    public static void queueEvent(Context context, PostRawData postRawData) {
        EventQueue eventQueue = getEventQueue(context).push(postRawData);
        instance(context).set(EVENT_QUEUE, eventQueue.toJson());
    }

    public static void setEventQueue(Context context, EventQueue eventQueue) {
        instance(context).set(EVENT_QUEUE, eventQueue.toJson());
    }
}
