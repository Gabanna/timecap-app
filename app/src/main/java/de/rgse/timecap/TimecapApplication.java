package de.rgse.timecap;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;

import de.rgse.timecap.service.TimecapProperties;
import de.rgse.timecap.service.UserData;


public class TimecapApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            if (!UserData.instance(this).has(UserData.SERVER_URL)) {
                UserData.instance(this).set(UserData.SERVER_URL, TimecapProperties.readProperty("rest.baseUrl"));
            }
            String url = String.format("%s/crashes", UserData.instance(this).get(UserData.SERVER_URL));
            final ACRAConfiguration config = new ConfigurationBuilder(this)
                    .setAlsoReportToAndroidFramework(true)
                    .setApplicationLogFile("timecap.log")
                    .setSendReportsInDevMode(true)
                    .setFormUri(url)
                    .setReportType(HttpSender.Type.JSON)
                    .setHttpMethod(HttpSender.Method.POST)
                    .setCustomReportContent(
                            ReportField.APPLICATION_LOG,
                            ReportField.ANDROID_VERSION,
                            ReportField.USER_EMAIL,
                            ReportField.STACK_TRACE,
                            ReportField.STACK_TRACE_HASH
                    )
                    .build();

            ACRA.init(this, config);

        } catch (ACRAConfigurationException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

    }
}
