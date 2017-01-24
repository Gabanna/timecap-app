package de.rgse.timecap.service;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by absolem on 24.01.17.
 */

public class TimecapProperties extends Properties {

    private static final TimecapProperties INSTANCE = new TimecapProperties();

    private TimecapProperties() {
        try {
            load(getClass().getResourceAsStream("/assets/timecap.properties"));
        } catch (IOException e) {
            Log.e(TimecapProperties.class.getSimpleName(), "unable to load properties", e);
            throw new RuntimeException(e);
        }
    }

    public static String readProperty(String name) {
        return INSTANCE.getProperty(name);
    }
}

