package de.rgse.timecap.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import de.rgse.timecap.fassade.JsonArray;
import de.rgse.timecap.fassade.JsonObject;

public class EventQueue implements  Iterable<PostRawData>{

    private Stack<PostRawData> data;

    public EventQueue(String json) {
        this(new JsonArray(json));
    }

    public EventQueue(JsonArray jsonArray) {
        data = new Stack<>();
        for(JsonObject jsonObject : jsonArray) {
            data.add(new PostRawData(jsonObject));
        }
    }

    public EventQueue push(PostRawData postRawData) {
        data.add(postRawData);
        return this;
    }

    public EventQueue pushAll(List<PostRawData> toPush) {
        data.addAll(toPush);
        return this;
    }

    public PostRawData pop() {
        PostRawData result = null;
        if (!data.isEmpty()) {
            result = data.pop();
        }
        return result;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    @Override
    public Iterator<PostRawData> iterator() {
        return data.iterator();
    }

    public String toJson() {
        JsonArray jsonArray = new JsonArray();
        for(PostRawData postRawData : data) {
            jsonArray.add(postRawData.toJson());
        }
        return jsonArray.toString();
    }
}
