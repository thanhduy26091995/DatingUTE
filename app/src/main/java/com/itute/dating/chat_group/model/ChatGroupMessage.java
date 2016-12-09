package com.itute.dating.chat_group.model;

import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 08/12/2016.
 */
public class ChatGroupMessage {
    private String content;
    private String sendBy;
    private String avatarSender;
    private String timestamp;

    public ChatGroupMessage() {
    }

    public ChatGroupMessage(String content, String sendBy, String avatarSender, String timestamp) {
        this.content = content;
        this.sendBy = sendBy;
        this.avatarSender = avatarSender;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public String getAvatarSender() {
        return avatarSender;
    }

    public void setAvatarSender(String avatarSender) {
        this.avatarSender = avatarSender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.TIMESTAMP, timestamp);
        myMap.put(Constants.CONTENT, content);
        myMap.put(Constants.SEND_BY, sendBy);
        myMap.put(Constants.AVATAR_SENDER, avatarSender);
        return myMap;
    }
}
