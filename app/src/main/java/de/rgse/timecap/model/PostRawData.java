package de.rgse.timecap.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by absolem on 23.01.17.
 */

public class PostRawData {

    private final String userId;
    private final String locationId;

    public PostRawData(String userId, String locationId){
        this.userId = userId;
        this.locationId = locationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocationId() {
        return locationId;
    }

    public JSONObject asJson() throws JSONException {
        JSONObject result = new JSONObject();

        if(userId != null && !userId.isEmpty()) {
            result.put("userId", userId);
        }

        if(locationId != null && !locationId.isEmpty()) {
            result.put("locationId", locationId);
        }

        return result;
    }
}
