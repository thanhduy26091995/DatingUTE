package com.itute.dating.chat_group.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.itute.dating.util.Constants;

import java.util.Map;

/**
 * Created by buivu on 08/12/2016.
 */
public class ChatGroupSubmitter {
    private DatabaseReference mDatabase;

    public ChatGroupSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //lấy toàn bộ danh sách chat của group
    public Query getAllChatGroup(String groupId) {
        return mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.CONTENTS);
    }


    //thêm data chat vào messages
    public void addChatGroupMessage(String groupId, ChatGroupMessage chatGroupMessage) {

        String key = mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.CONTENTS).push().getKey();
        Map<String, Object> myMap = chatGroupMessage.toMap();
        mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.CONTENTS).child(key).setValue(myMap);
    }
}
