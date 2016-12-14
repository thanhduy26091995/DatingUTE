package com.itute.dating.chat_group.model;

/**
 * Created by buivu on 14/12/2016.
 */
public class ChatGroupInfo {
    private String groupName;
    private String groupAvatar;

    public ChatGroupInfo() {
    }

    public ChatGroupInfo(String groupName, String groupAvatar) {
        this.groupName = groupName;
        this.groupAvatar = groupAvatar;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }
}
