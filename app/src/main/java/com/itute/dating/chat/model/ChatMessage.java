package com.itute.dating.chat.model;

import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 13/10/2016.
 */
public class ChatMessage {
    private boolean isMine;
    private String fromName;
    private String message;
    private long timestamp;
    private String toName;

    public ChatMessage() {

    }

    public ChatMessage(boolean isMine, String fromName, String message, long timestamp, String toName) {
        this.isMine = isMine;
        this.fromName = fromName;
        this.message = message;
        this.timestamp = timestamp;
        this.toName = toName;
    }

    public ChatMessage(String fromName, String message, long timestamp, String toName) {
        this.fromName = fromName;
        this.message = message;
        this.timestamp = timestamp;
        this.toName = toName;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public void setIsMine(boolean mine) {
        isMine = mine;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.IS_MINE, isMine);
        myMap.put(Constants.FROM_NAME, fromName);
        myMap.put(Constants.MESSAGE, message);
        myMap.put(Constants.TIMESTAMP, timestamp);
        myMap.put(Constants.TO_NAME, toName);
        return myMap;
    }
}
