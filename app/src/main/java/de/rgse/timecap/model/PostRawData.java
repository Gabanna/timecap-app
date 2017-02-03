package de.rgse.timecap.model;

import java.util.Calendar;

import de.rgse.timecap.fassade.JsonObject;
import de.rgse.timecap.service.IOUtil;

public class PostRawData {

    private final String userId;
    private final String locationId;
    private String instant;

    public PostRawData(JsonObject jsonObject) {
        this.userId = jsonObject.get("userId");
        this.locationId = jsonObject.get("locationId");
        this.instant = jsonObject.get("instant");
    }

    public PostRawData(String userId, String locationId, Calendar instant) {
        this.instant = null == instant ? null : IOUtil.formatDate(instant.getTime());
        this.userId = userId;
        this.locationId = locationId;
    }

    public PostRawData(String userId, String locationId) {
        this(userId, locationId, null);
    }

    public String getUserId() {
        return userId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getInstant() {
        return instant;
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        if (userId != null && !userId.isEmpty()) {
            result.set("userId", userId);
        }

        if (locationId != null && !locationId.isEmpty()) {
            result.set("locationId", locationId);
        }


        if (instant != null && !instant.isEmpty()) {
            result.set("instant", instant);
        }

        return result;
    }

    public void setInstant(Calendar instant) {
        this.instant = IOUtil.formatDate(instant.getTime());
    }
}
