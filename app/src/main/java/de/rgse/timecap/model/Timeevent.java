package de.rgse.timecap.model;

import android.content.Intent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.service.UserData;

public class Timeevent {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss");

    private final Logger LOGGER = LogManager.getLogManager().getLogger(getClass().getSimpleName());

    @JsonInclude(Include.NON_NULL)
    private String id;

    @JsonInclude(Include.NON_NULL)
    private String instant;

    @JsonIgnore
    private Calendar instantAsCalendar;

    private String userId;

    private String locationId;

    public String getId() {
        return id;
    }

    public String getInstant() {
        return instant;
    }

    public Timeevent() {
    }

    public Calendar getInstantAsCalendar() {
        try {
            if (instantAsCalendar == null && instant != null) {
                instantAsCalendar = GregorianCalendar.getInstance();
                instantAsCalendar.setTime(DATE_FORMAT.parse(instant));
            }
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error while parsing instant", e);
        }

        return instantAsCalendar;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getDate() {
        Calendar calendar = getInstantAsCalendar();
        return String.format("%02d. %02d. %s", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    public String getTime() {
        Calendar calendar = getInstantAsCalendar();
        return String.format("%02d:%02d:%02d Uhr", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public int getDay() {
        Calendar calendar = getInstantAsCalendar();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public Timeevent setInstant(Date instant) {
        this.instant = DATE_FORMAT.format(instant);
        instantAsCalendar = Calendar.getInstance();
        instantAsCalendar.setTime(instant);
        return this;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .set("id", id)
                .set("instant", instant)
                .set("userId", userId)
                .set("locationId", locationId);
    }

    public static Timeevent fromJson(JsonObject jsonObject) {
        Timeevent timeevent = new Timeevent();
        timeevent.id = jsonObject.get("id");
        timeevent.instant = jsonObject.get("instant");
        timeevent.userId = jsonObject.get("userId");
        timeevent.locationId = jsonObject.get("locationId");

        return timeevent;
    }
}
