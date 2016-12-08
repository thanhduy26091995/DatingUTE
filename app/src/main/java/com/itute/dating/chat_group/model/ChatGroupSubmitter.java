package com.itute.dating.chat_group.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 05/12/2016.
 */
public class ChatGroupSubmitter {
    private DatabaseReference mDatabase;

    public ChatGroupSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //lấy danh sách bạn bè
    public Query getAlllFriends(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.FRIENDS);
    }

    //thêm thành viên vào nhóm
    public void addMemberToGroup(List<String> listMember) {
        String key = mDatabase.child(Constants.CHAT_GROUP).push().getKey();
        Map<String, Object> myMap = new HashMap<>();
        for (int i = 0; i < listMember.size(); i++) {
            myMap.put(String.valueOf(i), listMember.get(i));
            //add id of groupchat
            addGroupToUser(listMember.get(i), key);
        }
        //add database
        mDatabase.child(Constants.CHAT_GROUP).child(key).child(Constants.MEMBER).setValue(myMap);
    }

    private void addGroupToUser(String userId, String groupChatId) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(groupChatId, true);
        mDatabase.child(Constants.USERS).child(userId).child(Constants.GROUPS).setValue(myMap);
    }
}
